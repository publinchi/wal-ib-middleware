using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.ComTypes;
using System.Text;
using System.Threading.Tasks;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using log4net.Config;
using Microsoft.Office.Interop.Word;
using Newtonsoft.Json.Linq;
using Table = Microsoft.Office.Interop.Word.Table;

namespace ConsolaNetReader
{
    internal class Program
    {
        static void Main(string[] args)

        {
            Application wordApp = new Application();
            Microsoft.Office.Interop.Word.Document doc = null;

            try
            {
                // Abre el documento .docx
                string filePath = @"C:\cobis\templates\plantilla.docx";
                doc = wordApp.Documents.Open(filePath);
                int i = 1;
                // Recorre las tablas en el documento
                foreach (Table table in doc.Tables)
                {
                    Console.WriteLine("TABLA "+i);


                    Console.WriteLine("Table found!");
                    // Puedes hacer más cosas con la tabla aquí, como recorrer sus filas y columnas
                    foreach (Row row in table.Rows)
                    {


                        foreach (Cell cell in row.Cells)
                        {
                            Console.WriteLine(cell.Range.Text.Trim());
                        }
                    }

                    ++i;
                }

                Console.ReadLine(); 
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: " + ex.Message);
            }
            finally
            {
                // Cierra el documento y la aplicación de Word
                if (doc != null)
                {
                    doc.Close();
                    wordApp.Quit();
                }
            }

        }


   
    }
}