using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data.SqlClient;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.ComTypes;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
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

        private static readonly byte[] Key = Encoding.UTF8.GetBytes("Xi9dA9/agZLXZiVBh0nXSitTngwfw35Y"); // La clave debe tener 32 caracteres
        private static readonly byte[] IV = Encoding.UTF8.GetBytes("TpSrOzrHlPBMPR3m"); // El vector de inicialización (IV) debe tener 16 caracteres
        public static async System.Threading.Tasks.Task Main(string[] args)

        {

          /*  string templates = ConfigurationManager.AppSettings["mail"];

            string correo= File.ReadAllText(templates);*/


            string cifred= Encrypt("dbad94ec5c744391077d7fac72ca4737a05ac06e0091bec8ccfb65e7309b1d539da851c2e38171cbb394db20543d67a5");

            DateTime fecha = DateTime.Now;
            string horario = String.Format("Fecha: {0:dd/MM/yy} - Hora: {0:hh:mm tt}", fecha).ToLower();
            Console.WriteLine(horario);

                ContractSend sender = new ContractSend();
              await  sender.EnviaContratoAsyncApi();

        }



        public static string Encrypt(string plainText)
        {
            using (Aes aesAlg = Aes.Create())
            {
                aesAlg.Key = Key;
                aesAlg.IV = IV;

                ICryptoTransform encryptor = aesAlg.CreateEncryptor(aesAlg.Key, aesAlg.IV);

                using (MemoryStream msEncrypt = new MemoryStream())
                {
                    using (CryptoStream csEncrypt = new CryptoStream(msEncrypt, encryptor, CryptoStreamMode.Write))
                    {
                        using (StreamWriter swEncrypt = new StreamWriter(csEncrypt))
                        {
                            swEncrypt.Write(plainText);
                        }
                        return Convert.ToBase64String(msEncrypt.ToArray());
                    }
                }
            }


        }
        }
}