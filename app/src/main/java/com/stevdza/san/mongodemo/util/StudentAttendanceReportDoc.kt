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
import com.stevdza.san.mongodemo.model.StudentAttendancePdf
import com.stevdza.san.mongodemo.util.conversion.isPastSignInTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

class StudentAttendanceReportDoc(private val context: Context
){

    suspend fun generateInvoicePdf(studentAttendancePdf: StudentAttendancePdf, outputStream: OutputStream) {
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

            val schoolName = Paragraph(studentAttendancePdf.schoolNme)
                .setBold()
                .setFontSize(28f)
//                .setBackgroundColor(DeviceRgb(0, 92, 230))
                .setPadding(12f)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(TextAlignment.CENTER)

            val tableTitle = Paragraph(studentAttendancePdf.title)
//                .setBold()
                .setFontSize(16f)
//                .setBackgroundColor(DeviceRgb(0, 92, 230))
//                .setPadding(12f)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(TextAlignment.CENTER)
//                .setWidth(100f)

            val date = Paragraph(studentAttendancePdf.date)
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
                createBoldTextParagraph("Student Name").setTextAlignment(TextAlignment.LEFT)
            val titleDate = createBoldTextParagraph("Date").setTextAlignment(TextAlignment.CENTER)
            val titleTimeIn = createBoldTextParagraph("Time In").setTextAlignment(TextAlignment.CENTER)
            val titleTimeOut = createBoldTextParagraph("Time Out").setTextAlignment(TextAlignment.RIGHT)

            val productsTable = Table(UnitValue.createPercentArray(floatArrayOf(5f, 3f, 1f, 1f))).apply {
                setMarginLeft(15f)
                setMarginRight(15f)
                setMarginTop(30f)

            }

            productsTable.addCell(createProductTableCell(titleSchoolName))
            productsTable.addCell(createProductTableCell(titleDate))
            productsTable.addCell(createProductTableCellTime(titleTimeIn))
            productsTable.addCell(createProductTableCellTime(titleTimeOut))

            val lighterBlack = DeviceRgb(64, 64, 64)
            val lighterRed = DeviceRgb(238, 75, 43)

            studentAttendancePdf.attendance.forEach {
                val timeInStudent = it.studentIn?.time ?: ""
                val broughtInBy = it.studentIn?.studentBroughtBy ?: ""
                val timeOutStudent = it.studentOut?.time ?: ""
                val broughtOutBy = it.studentOut?.studentBroughtBy ?: ""
                val isPastSignInTime = isPastSignInTime(timeInStudent, studentAttendancePdf.hourIn, studentAttendancePdf.minIn)
                val isPastSignOutTime = isPastSignInTime(timeOutStudent, studentAttendancePdf.hourOut, studentAttendancePdf.minOut)
                val name = createSmallBoldTextParagraph(
                    """
                        ${it.studentName}
                        In -> $broughtInBy  Out -> $broughtOutBy
                    """.trimIndent(),
                    lighterBlack
                ).setTextAlignment(TextAlignment.LEFT)

                val dateList = createSmallBoldTextParagraph(
                    it.date,
                    lighterBlack
                ).setTextAlignment(TextAlignment.CENTER)

                val timeIn = createSmallBoldTextParagraph(
                    timeInStudent,
                    if (isPastSignInTime){
                        lighterRed
                    }else{
                        lighterBlack
                    }

                ).setTextAlignment(TextAlignment.CENTER)

                val timeOut = createSmallBoldTextParagraph(
                    timeOutStudent,
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



            val summary =
                createBoldTextParagraph("Summary").setTextAlignment(TextAlignment.LEFT)
            val days = createBoldTextParagraph("Days").setTextAlignment(TextAlignment.CENTER)
            val percentage = createBoldTextParagraph("Percentage").setTextAlignment(TextAlignment.CENTER)

            val present = createSmallBoldTextParagraph("Present", lighterBlack).setTextAlignment(TextAlignment.LEFT)
            val daysPresent = createSmallBoldTextParagraph(studentAttendancePdf.presentDays, lighterBlack).setTextAlignment(TextAlignment.CENTER)
            val presentPercent = createSmallBoldTextParagraph(studentAttendancePdf.presentDaysPercent, lighterBlack).setTextAlignment(TextAlignment.CENTER)

            val absent = createSmallBoldTextParagraph("Absent", lighterBlack).setTextAlignment(TextAlignment.LEFT)
            val daysAbsent = createSmallBoldTextParagraph(studentAttendancePdf.absentDays, lighterBlack).setTextAlignment(TextAlignment.CENTER)
            val absentPercent = createSmallBoldTextParagraph(studentAttendancePdf.absentDaysPercent, lighterBlack).setTextAlignment(TextAlignment.CENTER)


            val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(6f, 2f, 2f))).apply {
                setMarginLeft(15f)
                setMarginRight(15f)
                setMarginTop(30f)

            }
            summaryTable.addCell(createProductTableCell(summary))
            summaryTable.addCell(createProductTableCell(days))
            summaryTable.addCell(createProductTableCell(percentage))

            summaryTable.addCell(createProductTableCell(present))
            summaryTable.addCell(createProductTableCell(daysPresent))
            summaryTable.addCell(createProductTableCell(presentPercent))

            summaryTable.addCell(createProductTableCell(absent))
            summaryTable.addCell(createProductTableCell(daysAbsent))
            summaryTable.addCell(createProductTableCell(absentPercent))



            document.add(productsTable)
            document.add(summaryTable)
            document.close()

        }
    }



    private fun createProductTableCell(paragraph: Paragraph): Cell {
        return Cell().add(paragraph).apply {
            setPaddingBottom(10f)
            setPaddingTop(10f)
            setBorder(null)
            setBorderBottom(SolidBorder(DeviceRgb(204, 204, 204), 1f))
        }
    }

    private fun createProductTableCellTime(paragraph: Paragraph): Cell {
        return Cell().add(paragraph).apply {
            setPaddingBottom(10f)
            setPaddingTop(10f)
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
            setFontSize(13f)
            setFontColor(color)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
        }
        return Paragraph(text).addStyle(boldTextStyle)
    }

    private fun createSmallBoldTextParagraph(text: String, color: Color = DeviceRgb.BLACK): Paragraph {
        val boldTextStyle = Style().apply {
            setFontSize(11f)
            setFontColor(color)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
        }
        return Paragraph(text).addStyle(boldTextStyle)
    }

    private fun createNoBorderCell(paragraph: Paragraph): Cell {
        return Cell().add(paragraph).setBorder(null)
    }


}