using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using DocumentFormat.OpenXml;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;

namespace ConsolaNetReader
{
    public  class DocxReplacer
    {
        public  byte[] ReemplazarEnMemoria(byte[] docxBytes, IDictionary<string, string> placeholders)
        {
            if (docxBytes == null || docxBytes.Length == 0)
                throw new ArgumentException("docxBytes vacío");
            if (placeholders == null || placeholders.Count == 0)
                return docxBytes;

            using (var ms = new MemoryStream())
            {
                ms.Write(docxBytes, 0, docxBytes.Length);
                ms.Position = 0;

                using (var doc = WordprocessingDocument.Open(ms, true))
                {
                    // Cuerpo principal
                    ReemplazarEnMainDocumentPart(doc.MainDocumentPart, placeholders);

                    // Encabezados
                    foreach (var hp in doc.MainDocumentPart.HeaderParts)
                        ReemplazarEnHeaderPart(hp, placeholders);

                    // Pies de página
                    foreach (var fp in doc.MainDocumentPart.FooterParts)
                        ReemplazarEnFooterPart(fp, placeholders);

                    // Notas al pie y notas al final (si existen)
                    if (doc.MainDocumentPart.FootnotesPart != null)
                        ReemplazarEnFootnotesPart(doc.MainDocumentPart.FootnotesPart, placeholders);

                    if (doc.MainDocumentPart.EndnotesPart != null)
                        ReemplazarEnEndnotesPart(doc.MainDocumentPart.EndnotesPart, placeholders);

                    doc.MainDocumentPart.Document.Save();
                }

                return ms.ToArray();
            }
        }

        private  void ReemplazarEnMainDocumentPart(MainDocumentPart mdp, IDictionary<string, string> map)
        {
            if (mdp == null || mdp.Document == null || mdp.Document.Body == null) return;
            ReemplazarEnOpenXmlElement((OpenXmlElement)mdp.Document.Body, map);
            mdp.Document.Save();
        }

        private  void ReemplazarEnHeaderPart(HeaderPart hp, IDictionary<string, string> map)
        {
            if (hp == null || hp.Header == null) return;
            ReemplazarEnOpenXmlElement((OpenXmlElement)hp.Header, map);
            hp.Header.Save();
        }

        private  void ReemplazarEnFooterPart(FooterPart fp, IDictionary<string, string> map)
        {
            if (fp == null || fp.Footer == null) return;
            ReemplazarEnOpenXmlElement((OpenXmlElement)fp.Footer, map);
            fp.Footer.Save();
        }

        private  void ReemplazarEnFootnotesPart(FootnotesPart part, IDictionary<string, string> map)
        {
            if (part == null || part.Footnotes == null) return;
            ReemplazarEnOpenXmlElement((OpenXmlElement)part.Footnotes, map);
            part.Footnotes.Save();
        }

        private  void ReemplazarEnEndnotesPart(EndnotesPart part, IDictionary<string, string> map)
        {
            if (part == null || part.Endnotes == null) return;
            ReemplazarEnOpenXmlElement((OpenXmlElement)part.Endnotes, map);
            part.Endnotes.Save();
        }

        /// <summary>
        /// Estrategia: por cada párrafo, concatenamos todos los w:t, aplicamos todos los reemplazos
        /// y volcamos el resultado en el primer w:t (vacíamos los demás). Mantiene el formato básico del primer run.
        /// </summary>
        private  void ReemplazarEnOpenXmlElement(OpenXmlElement root, IDictionary<string, string> map)
        {
            if (root == null) return;

            foreach (var para in root.Descendants<Paragraph>())
            {
                var texts = para.Descendants<Text>().ToList();
                if (texts.Count == 0) continue;

                // Concatenamos el texto del párrafo
                var sb = new StringBuilder(256);
                for (int i = 0; i < texts.Count; i++)
                    sb.Append(texts[i].Text ?? string.Empty);

                string original = sb.ToString();
                string replaced = original;

                // Reemplazos con comparación ordinal (case-sensitive, cultura-invariante)
                foreach (var kv in map)
                {
                    var key = kv.Key ?? string.Empty;
                    var val = kv.Value ?? string.Empty;
                    if (key.Length == 0) continue;
                    replaced = ReplaceAllOrdinal(replaced, key, val);
                }

                // Si no cambió, sigue
                if (!string.Equals(replaced, original, StringComparison.Ordinal))
                {
                    // Escribimos todo en el primer w:t y vaciamos el resto
                    SetPreservingText(texts[0], replaced);
                    for (int i = 1; i < texts.Count; i++)
                        SetPreservingText(texts[i], string.Empty);
                }
            }
        }

        /// <summary>
        /// Reemplazo ordinal manual (compatible con .NET antiguo / C# 7.3).
        /// </summary>
        private  string ReplaceAllOrdinal(string input, string oldValue, string newValue)
        {
            if (string.IsNullOrEmpty(input) || string.IsNullOrEmpty(oldValue))
                return input ?? string.Empty;

            var sb = new StringBuilder(input.Length);
            int pos = 0;
            int idx;

            while ((idx = input.IndexOf(oldValue, pos, StringComparison.Ordinal)) >= 0)
            {
                sb.Append(input, pos, idx - pos);
                if (!string.IsNullOrEmpty(newValue))
                    sb.Append(newValue);
                pos = idx + oldValue.Length;
            }

            sb.Append(input, pos, input.Length - pos);
            return sb.ToString();
        }

        /// <summary>
        /// Ajusta xml:space="preserve" si hay espacios al inicio/fin.
        /// </summary>
        private  void SetPreservingText(Text t, string value)
        {
            if (t == null) return;

            t.Text = value ?? string.Empty;

            bool needsPreserve = false;
            if (!string.IsNullOrEmpty(t.Text))
            {
                char first = t.Text[0];
                char last = t.Text[t.Text.Length - 1];
                if (char.IsWhiteSpace(first) || char.IsWhiteSpace(last))
                    needsPreserve = true;
            }

            // En C# 7.3 evita el operador ?: aquí
            if (needsPreserve)
            {
                // EnumValue<T> tiene conversión implícita desde T
                t.Space = SpaceProcessingModeValues.Preserve;
                // // Alternativa explícita:
                // t.Space = new EnumValue<SpaceProcessingModeValues>(SpaceProcessingModeValues.Preserve);
            }
            else
            {
                t.Space = null; // ok porque EnumValue<T> es una clase (referencia)
            }
        }

        public  byte[] AppendBeneficiariosToLastTable(byte[] docxBytes, List<Beneficiario> beneficiarios, bool agregarSignoPorciento = true)
        {
            if (docxBytes == null || docxBytes.Length == 0) throw new ArgumentException("docxBytes vacío");

            using (var ms = new MemoryStream())
            {
                ms.Write(docxBytes, 0, docxBytes.Length);
                ms.Position = 0;

                using (var doc = WordprocessingDocument.Open(ms, true))
                {
                    var body = doc.MainDocumentPart.Document.Body;
                    if (body == null) throw new InvalidOperationException("El documento no tiene Body.");

                    var lastTable = body.Descendants<Table>().LastOrDefault();
                    if (lastTable == null) throw new InvalidOperationException("No se encontró ninguna tabla.");

                    var allRows = lastTable.Elements<TableRow>().ToList();
                    if (allRows.Count == 0) throw new InvalidOperationException("La tabla no tiene filas.");

                    // Última fila no-encabezado como plantilla
                    TableRow templateRow = null;
                    for (int i = allRows.Count - 1; i >= 0; i--)
                    {
                        var rp = allRows[i].GetFirstChild<TableRowProperties>();
                        bool isHeader = rp != null && rp.Elements<TableHeader>().Any();
                        if (!isHeader)
                        {
                            templateRow = allRows[i];
                            break;
                        }
                    }
                    if (templateRow == null) templateRow = allRows.Last();

                    // Si lista nula o vacía → agregar fila "No Designados"
                    if (beneficiarios == null || beneficiarios.Count == 0)
                    {
                        var ndRow = (TableRow)templateRow.CloneNode(true);
                        var ndCells = ndRow.Elements<TableCell>().ToList();
                        while (ndCells.Count < 3)
                        {
                            var extra = new TableCell(new Paragraph(new Run(new Text(""))));
                            ndRow.AppendChild(extra);
                            ndCells.Add(extra);
                        }

                        ClearCellKeepProps(ndCells[0]);
                        ClearCellKeepProps(ndCells[1]);
                        ClearCellKeepProps(ndCells[2]);

                        SetCellSingleRunText(ndCells[0], "No Designados");
                        SetCellSingleRunText(ndCells[1], "No Designados");
                        SetCellSingleRunText(ndCells[2], "No Designados");

                        lastTable.AppendChild(ndRow);

                        doc.MainDocumentPart.Document.Save();
                        return ms.ToArray();
                    }

                    // Insertar filas por beneficiario
                    foreach (var b in beneficiarios)
                    {
                        var newRow = (TableRow)templateRow.CloneNode(true);

                        var cells = newRow.Elements<TableCell>().ToList();
                        while (cells.Count < 3)
                        {
                            var extraCell = new TableCell(new Paragraph(new Run(new Text(""))));
                            newRow.AppendChild(extraCell);
                            cells.Add(extraCell);
                        }

                        ClearCellKeepProps(cells[0]);
                        ClearCellKeepProps(cells[1]);
                        ClearCellKeepProps(cells[2]);

                        // Columna 1: "paterno, materno y nombre(s)"
                        var nombreComp = FormatearNombreCompleto(b);
                        SetCellSingleRunText(cells[0], nombreComp);

                        // Columna 2: fecha en formato "yyyy-MM-dd HH:mm:ss"
                        var fechaFmt = FormatearFechaNacimiento(b != null ? b.fechaNacimiento : null);
                        SetCellSingleRunText(cells[1], fechaFmt);

                        // Columna 3: porcentaje (+% si falta)
                        var pct = b != null ? (b.porcentaje ?? string.Empty) : string.Empty;
                        if (agregarSignoPorciento && !string.IsNullOrWhiteSpace(pct) && !pct.TrimEnd().EndsWith("%"))
                            pct = pct.Trim() + "%";
                        SetCellSingleRunText(cells[2], pct);

                        lastTable.AppendChild(newRow);
                    }

                    doc.MainDocumentPart.Document.Save();
                }

                return ms.ToArray();
            }
        }

        /// <summary>
        /// Pipeline: placeholders + beneficiarios (incluye "No Designados" si la lista viene vacía).
        /// </summary>
        public  byte[] ReemplazarYAgregarBeneficiarios(byte[] docxBytes, IDictionary<string, string> placeholders, List<Beneficiario> beneficiarios, bool agregarSignoPorciento = true)
        {
            var filled = ReemplazarEnMemoria(docxBytes, placeholders);
            return AppendBeneficiariosToLastTable(filled, beneficiarios, agregarSignoPorciento);
        }

        // =======================
        //  Helpers de tabla
        // =======================
        private  string FormatearNombreCompleto(Beneficiario b)
        {
            if (b == null) return string.Empty;
            var paterno = (b.paterno ?? "").Trim();
            var materno = (b.materno ?? "").Trim();
            var nombre = (b.nombre ?? "").Trim();

            if (paterno.Length == 0 && materno.Length == 0) return nombre;
            if (paterno.Length > 0 && materno.Length == 0) return paterno + " y " + nombre;
            if (paterno.Length == 0 && materno.Length > 0) return materno + " y " + nombre;
            return paterno + ", " + materno + " y " + nombre;
        }

        private  string FormatearFechaNacimiento(string fechaTexto)
        {
            if (string.IsNullOrWhiteSpace(fechaTexto)) return string.Empty;

            DateTime dt;
            // Intenta exacto primero si ya viene en tu formato objetivo
            if (DateTime.TryParseExact(fechaTexto, "yyyy-MM-dd HH:mm:ss", System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.None, out dt))
                return dt.ToString("yyyy-MM-dd HH:mm:ss");

            // Intenta varios formatos comunes
            string[] formatos =
            {
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "dd/MM/yyyy HH:mm:ss",
            "MM/dd/yyyy",
            "MM/dd/yyyy HH:mm:ss",
            "yyyy-MM-ddTHH:mm:ss",
            "yyyy-MM-ddTHH:mm:ss.fff"
        };
            if (DateTime.TryParseExact(fechaTexto, formatos, System.Globalization.CultureInfo.InvariantCulture, System.Globalization.DateTimeStyles.None, out dt) ||
                DateTime.TryParse(fechaTexto, out dt))
            {
                return dt.ToString("yyyy-MM-dd HH:mm:ss");
            }

            // Si no parsea, deja el texto original (o retorna vacío si prefieres)
            return fechaTexto;
        }

        private  void ClearCellKeepProps(TableCell cell)
        {
            if (cell == null) return;

            var tcp = cell.GetFirstChild<TableCellProperties>();
            var paragraphs = cell.Elements<Paragraph>().ToList();
            foreach (var p in paragraphs) p.Remove();

            cell.AppendChild(new Paragraph(new Run(new Text(string.Empty))));

            if (tcp != null)
            {
                cell.RemoveChild(tcp);
                cell.PrependChild(tcp);
            }
        }

        private  void SetCellSingleRunText(TableCell cell, string value)
        {
            if (cell == null) return;

            var p = cell.Elements<Paragraph>().FirstOrDefault();
            if (p == null)
            {
                p = new Paragraph();
                cell.AppendChild(p);
            }

            var runs = p.Elements<Run>().ToList();
            foreach (var run in runs) run.Remove();

            var runNew = new Run();

            var lines = (value ?? string.Empty).Split(new[] { "\r\n", "\n" }, StringSplitOptions.None);
            for (int i = 0; i < lines.Length; i++)
            {
                var t = new Text(lines[i]);
                if (lines[i].Length > 0 &&
                    (char.IsWhiteSpace(lines[i][0]) || char.IsWhiteSpace(lines[i][lines[i].Length - 1])))
                {
                    t.Space = SpaceProcessingModeValues.Preserve;
                }
                runNew.AppendChild(t);

                if (i < lines.Length - 1)
                    runNew.AppendChild(new Break());
            }

            p.AppendChild(runNew);
        }

    }
}
