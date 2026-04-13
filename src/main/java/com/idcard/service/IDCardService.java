package com.idcard.service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idcard.entity.IDCard;
import com.idcard.entity.User;
import com.idcard.repository.IDCardRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class IDCardService {

    private final IDCardRepository idCardRepository;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    public IDCardService(IDCardRepository idCardRepository) {
        this.idCardRepository = idCardRepository;
    }

    public byte[] getPDFBytes(Long cardId) throws IOException {
        if (cardId == null) {
            throw new IllegalArgumentException("Card ID cannot be null");
        }
        IDCard card = idCardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));
        Path pdfPath = Paths.get(card.getPdfPath());
        if (!Files.exists(pdfPath)) throw new FileNotFoundException("PDF missing: " + pdfPath);
        return Files.readAllBytes(pdfPath);
    }

    @Transactional
    public IDCard generateIDCard(User user) {
        try {
            String cardNumber = "IDC-" + System.currentTimeMillis();
            String pdfRelPath = savePDF(user, cardNumber);

            IDCard card = idCardRepository.findByUser(user).orElseGet(() -> {
                IDCard c = new IDCard();
                c.setUser(user);
                return c;
            });

            card.setCardNumber(cardNumber);
            card.setIssuedDate(LocalDateTime.now());
            card.setExpiryDate(LocalDateTime.now().plusYears(2));
            card.setPdfPath(pdfRelPath);
            card.setCardStatus(IDCard.CardStatus.ACTIVE);
            return idCardRepository.save(card);
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("ID card generation failed: " + e.getMessage(), e);
        }
    }

    private String savePDF(User user, String cardNumber) throws IOException {
        String cardDir = uploadDir + "cards/";
        Files.createDirectories(Paths.get(cardDir));
        String filename = "idcard_" + UUID.randomUUID() + ".pdf";
        String fullPath = cardDir + filename;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf  = new PdfDocument(writer);
            Document doc     = new Document(pdf, PageSize.A6.rotate());
            doc.setMargins(12, 16, 12, 16);

            DeviceRgb navy    = new DeviceRgb(15, 52, 96);
            DeviceRgb accent  = new DeviceRgb(0, 180, 216);
            DeviceRgb lightBg = new DeviceRgb(245, 248, 252);

            // Header
            String org = user.getOrganization() != null ? user.getOrganization() : "Organization";
            Paragraph header = new Paragraph(org)
                    .setFontSize(13).setBold()
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(navy)
                    .setPadding(8);
            doc.add(header);

            // Sub header
            Paragraph sub = new Paragraph("IDENTITY CARD")
                    .setFontSize(7).setBold()
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(accent)
                    .setPadding(3).setMarginTop(0);
            doc.add(sub);

            // Body
            Table body = new Table(new float[]{80f, 1f})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(8);

            // Photo cell
            Cell photoCell = new Cell().setBorder(null).setPadding(4);
            String photoFullPath = uploadDir + (user.getPhotoPath() != null ? user.getPhotoPath() : "");
            if (user.getPhotoPath() != null && Files.exists(Paths.get(photoFullPath))) {
                try {
                    Image img = new Image(ImageDataFactory.create(photoFullPath))
                            .setWidth(72).setHeight(90)
                            .setBorder(new SolidBorder(navy, 2f));
                    photoCell.add(img);
                } catch (Exception ex) {
                    photoCell.add(new Paragraph("[Photo]").setFontSize(8).setFontColor(ColorConstants.GRAY));
                }
            } else {
                photoCell.add(new Paragraph("[No Photo]").setFontSize(8).setFontColor(ColorConstants.GRAY));
            }
            body.addCell(photoCell);

            // Info cell
            Cell infoCell = new Cell().setBorder(null).setPadding(6).setBackgroundColor(lightBg);
            infoCell.add(new Paragraph(user.getFullName() != null ? user.getFullName() : "—")
                    .setBold().setFontSize(11).setFontColor(navy).setMarginBottom(4));
            addRow(infoCell, "Designation", user.getDesignation(), navy);
            addRow(infoCell, "Department",  user.getDepartment(), navy);
            addRow(infoCell, "Emp ID",      user.getEmployeeId(), navy);
            addRow(infoCell, "Blood Group", user.getBloodGroup(), navy);
            addRow(infoCell, "Phone",       user.getPhoneNumber(), navy);
            body.addCell(infoCell);
            doc.add(body);

            // Footer
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String validity = "Valid till: " + LocalDateTime.now().plusYears(2).format(fmt);
            Table footer = new Table(new float[]{1f, 1f})
                    .setWidth(UnitValue.createPercentValue(100)).setMarginTop(8);
            footer.addCell(new Cell().setBorder(null)
                    .add(new Paragraph("Card: " + cardNumber).setFontSize(6.5f).setFontColor(ColorConstants.DARK_GRAY)));
            footer.addCell(new Cell().setBorder(null)
                    .add(new Paragraph(validity).setFontSize(6.5f)
                            .setTextAlignment(TextAlignment.RIGHT).setFontColor(ColorConstants.DARK_GRAY)));
            doc.add(footer);
            doc.close();
            Files.write(Paths.get(fullPath), baos.toByteArray());
        }
        return fullPath;
    }

    private void addRow(Cell cell, String label, String value, DeviceRgb labelColor) {
        if (value == null || value.isBlank()) return;
        cell.add(new Paragraph()
                .add(new Text(label + ": ").setBold().setFontSize(7.5f).setFontColor(labelColor))
                .add(new Text(value).setFontSize(7.5f))
                .setMarginBottom(2));
    }
}
