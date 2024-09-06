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
            ContractSend sender = new ContractSend();
            sender.EnviaContrato();

        }


   
    }
}