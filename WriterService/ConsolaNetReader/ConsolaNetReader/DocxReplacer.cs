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

    }
}
