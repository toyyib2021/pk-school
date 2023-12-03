package com.stevdza.san.mongodemo.util

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.Style
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.Leading
import com.itextpdf.layout.property.Property
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.itextpdf.layout.property.VerticalAlignment
import com.stevdza.san.mongodemo.model.StaffAttendancePdf
import com.stevdza.san.mongodemo.util.conversion.isPastSignInTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

class MonthlyAttendanceReportDoc(private val context: Context
){

    suspend fun generateInvoicePdf(staffAttendancePdf: StaffAttendancePdf, outputStream: OutputStream) {
        withContext(Dispatchers.IO) {
//            val outPutFile = File(context.filesDir, "test.pdf")
            val pdfWriter = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A4).apply {
//                setMargins(50f, 13f, 13f, 13f)

                setProperty(
                    Property.LEADING,
                    Leading(Leading.MULTIPLIED, 1f)
                )
            }.setWordSpacing(0f)

            val page = pdfDocument.addNewPage()
            Log.e(TAG, "generateInvoicePdf: ${page.pageSize.width}", )

            val schoolName = Paragraph(staffAttendancePdf.schoolNme)
                .setBold()
                .setFontSize(32f)
//                .setBackgroundColor(DeviceRgb(0, 92, 230))
                .setPadding(12f)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(TextAlignment.CENTER)

            val tableTitle = Paragraph(staffAttendancePdf.title)
//                .setBold()
                .setFontSize(20f)
//                .setBackgroundColor(DeviceRgb(0, 92, 230))
//                .setPadding(12f)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(TextAlignment.CENTER)
//                .setWidth(100f)

            val date = Paragraph(staffAttendancePdf.date)
//                .setBold()
                .setFontSize(14f)
//                .setBackgroundColor(DeviceRgb(0, 92, 230))
                .setPadding(7f)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                .setTextAlignment(TextAlignment.RIGHT)

            // Top Section Table
            val schoolNameTable = Table(1, true)
            schoolNameTable.addCell(createNoBorderCell(schoolName))

            val titleTable = Table(1, true)
            schoolNameTable.addCell(createNoBorderCell(tableTitle))
            titleTable.addCell(createNoBorderCell(date))


            // Top Section Separator
            val line = LineSeparator(
                SolidLine().apply {
                    color = DeviceRgb(204, 204, 204)
                }
            ).setMarginTop(20f)

            document.add(schoolNameTable)
            document.add(titleTable)
            document.add(line)



            // Products Table
            val titleSchoolName =
                createBoldTextParagraph("Staff Name").setTextAlignment(TextAlignment.LEFT)
            val titleDate = createBoldTextParagraph("Date").setTextAlignment(TextAlignment.CENTER)
            val titleTimeIn = createBoldTextParagraph("Time In").setTextAlignment(TextAlignment.CENTER)
            val titleTimeOut = createBoldTextParagraph("Time Out").setTextAlignment(TextAlignment.RIGHT)

            val productsTable = Table(UnitValue.createPercentArray(floatArrayOf(5f, 3f, 1f, 1f))).apply {
                setMarginLeft(15f)
                setMarginRight(15f)
                setMarginTop(50f)

            }

            productsTable.addCell(createProductTableCell(titleSchoolName))
            productsTable.addCell(createProductTableCell(titleDate))
            productsTable.addCell(createProductTableCellTime(titleTimeIn))
            productsTable.addCell(createProductTableCellTime(titleTimeOut))

            val lighterBlack = DeviceRgb(64, 64, 64)
            val lighterRed = DeviceRgb(238, 75, 43)

            staffAttendancePdf.attendance.forEach {
                val isPastSignInTime = isPastSignInTime(it.timeIn, staffAttendancePdf.hourIn, staffAttendancePdf.minIn)
                val isPastSignOutTime = isPastSignInTime(it.timeOut, staffAttendancePdf.hourOut, staffAttendancePdf.minOut)
                val name = createSmallBoldTextParagraph(
                    it.name,
                    lighterBlack
                ).setTextAlignment(TextAlignment.LEFT)

                val dateList = createSmallBoldTextParagraph(
                    it.date,
                    lighterBlack
                ).setTextAlignment(TextAlignment.CENTER)

                val timeIn = createSmallBoldTextParagraph(
                    it.timeIn,
                    if (isPastSignInTime){
                        lighterRed
                    }else{
                        lighterBlack
                    }

                ).setTextAlignment(TextAlignment.CENTER)

                val timeOut = createSmallBoldTextParagraph(
                    it.timeOut,
                    if (isPastSignOutTime){
                        lighterBlack
                    }else{
                        lighterRed

                    }
                ).setTextAlignment(TextAlignment.CENTER)

                productsTable.addCell(createProductTableCell(name))
                productsTable.addCell(createProductTableCell(dateList))
                productsTable.addCell(createProductTableCellTime(timeIn))
                productsTable.addCell(createProductTableCellTime(timeOut))
            }


//            val grantTotal = Paragraph("Grand Total")
//                .setFontColor(DeviceRgb(166, 166, 166))
//                .setFontSize(16f)
//                .setTextAlignment(TextAlignment.RIGHT)
//            val grandTotalCell = Cell(1, 4).add(grantTotal).setBorder(null)
//                .setBorderBottom(SolidBorder(DeviceRgb(204, 204, 204), 2f))
//                .setPaddingTop(20f)
//                .setPaddingBottom(20f)
//            productsTable.addCell(grandTotalCell)



            document.add(productsTable)
            document.close()
        }
    }


    private fun createProductTableCell(paragraph: Paragraph): Cell {
        return Cell().add(paragraph).apply {
            setPaddingBottom(20f)
            setPaddingTop(15f)
            setWidth(200f)
            setBorder(null)
            setBorderBottom(SolidBorder(DeviceRgb(204, 204, 204), 1f))
        }
    }

    private fun createProductTableCellTime(paragraph: Paragraph): Cell {
        return Cell().add(paragraph).apply {
            setPaddingBottom(20f)
            setPaddingTop(15f)
            setWidth(120f)
            setBorder(null)
            setBorderBottom(SolidBorder(DeviceRgb(204, 204, 204), 1f))
        }
    }

     fun createLightTextParagraph(text: String): Paragraph {
        val lightTextStyle = Style().apply {
            setFontSize(12f)
            setFontColor(DeviceRgb(166, 166, 166))
        }
        return Paragraph(text).addStyle(lightTextStyle)
    }

     private fun createBoldTextParagraph(text: String, color: Color = DeviceRgb.BLACK): Paragraph {
        val boldTextStyle = Style().apply {
            setFontSize(16f)
            setFontColor(color)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
        }
        return Paragraph(text).addStyle(boldTextStyle)
    }

    private fun createSmallBoldTextParagraph(text: String, color: Color = DeviceRgb.BLACK): Paragraph {
        val boldTextStyle = Style().apply {
            setFontSize(14f)
            setFontColor(color)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
        }
        return Paragraph(text).addStyle(boldTextStyle)
    }

    private fun createNoBorderCell(paragraph: Paragraph): Cell {
        return Cell().add(paragraph).setBorder(null)
    }


}