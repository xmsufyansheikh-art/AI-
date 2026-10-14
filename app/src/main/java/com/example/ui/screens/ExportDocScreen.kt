package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.export.FileExportHelper
import com.example.ui.theme.DarkBlueBorder
import com.example.ui.theme.ElectricSkyBlue
import com.example.ui.theme.GulaboRose
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.GulaboViewModel

@Composable
fun ExportDocScreen(
    viewModel: GulaboViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val docTitle by viewModel.docTitle.collectAsState()
    val docContent by viewModel.docContent.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = ElectricSkyBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "PDF & Document Generator",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Export reports, essays, translations & chats into styled PDF or text files",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Quick Templates Loader
        Text(
            text = "⚡ Quick Load Templates:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .clickable { viewModel.populateDocFromChat() }
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Load from Chat", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clickable {
                        viewModel.setDocTitle("Gulabo AI • Multilingual Research Summary")
                        viewModel.setDocContent(
                            "EXECUTIVE INTELLIGENCE SUMMARY\n\n" +
                            "Subject: Advanced Multilingual Artificial Intelligence System\n" +
                            "Architect: Gulabo AI Cloud Platform\n\n" +
                            "1. Overview\n" +
                            "Gulabo AI seamlessly unites 50+ languages with deep reasoning, instant bidirectional translation, multimodal computer vision, and video generation.\n\n" +
                            "2. Key Pillars\n" +
                            "• Real-Time Voice Synthesis in native accents (Urdu, Arabic, Hindi, English)\n" +
                            "• Veo Cinematography Storyboard & Picture-to-Video Synthesis\n" +
                            "• Zero-latency local PDF rendering and file dispatch"
                        )
                    }
                    .border(1.dp, DarkBlueBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Executive Summary", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clickable {
                        viewModel.setDocTitle("گلابو اردو ادب و شاعری مجموعہ")
                        viewModel.setDocContent(
                            "گلابو اے آئی — شاہکار اردو شاعری و ادب\n\n" +
                            "ستاروں سے آگے جہاں اور بھی ہیں\nابھی عشق کے امتحاں اور بھی ہیں\n\n" +
                            "ہزاروں خواہشیں ایسی کہ ہر خواہش پہ دم نکلے\nبہت نکلے مرے ارمان لیکن پھر بھی کم نکلے\n\n" +
                            "گلاب کی طرح مہکتی ہوئی یہ سوچ اور شعور، کلاؤڈ انٹیلی جنس کے ساتھ پیش کی گئی۔"
                        )
                    }
                    .border(1.dp, DarkBlueBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Urdu Literature", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Title Field
        OutlinedTextField(
            value = docTitle,
            onValueChange = { viewModel.setDocTitle(it) },
            label = { Text("Document Title") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("doc_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = ElectricSkyBlue,
                unfocusedBorderColor = DarkBlueBorder
            )
        )

        // Body Content Field
        OutlinedTextField(
            value = docContent,
            onValueChange = { viewModel.setDocContent(it) },
            label = { Text("Document Content / Essay / Report") },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .testTag("doc_content_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = ElectricSkyBlue,
                unfocusedBorderColor = DarkBlueBorder
            )
        )

        // Primary Action Buttons
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { viewModel.exportCurrentDocToPdf() },
                enabled = docContent.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("generate_pdf_button")
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate & Share Styled PDF Document", fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.exportCurrentDocToTxt() },
                    enabled = docContent.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f).testTag("export_txt_button")
                ) {
                    Icon(Icons.Default.TextSnippet, contentDescription = null, tint = ElectricSkyBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export .TXT", color = MaterialTheme.colorScheme.onSurface)
                }

                Button(
                    onClick = {
                        FileExportHelper.copyToClipboard(context, "$docTitle\n\n$docContent")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ElectricSkyBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy All", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // Live PDF Layout Simulation Box
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBlueBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "📄 PDF Document Page Preview:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElectricSkyBlue
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Simulated Sheet of Paper
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // PDF Header Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .background(MidnightNavy)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "GULABO AI • INTELLIGENT REPORT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Rose line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(GulaboRose)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (docTitle.isBlank()) "Document Title" else docTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (docContent.isBlank()) "Content will appear here..." else docContent.take(300) + if (docContent.length > 300) "..." else "",
                            fontSize = 10.sp,
                            lineHeight = 15.sp,
                            color = Color(0xFF334155)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Generated by Gulabo AI • Multilingual Cloud Intelligence",
                            fontSize = 8.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}
