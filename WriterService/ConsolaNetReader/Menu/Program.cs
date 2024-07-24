using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace DocumentServices
{
    internal static class Program
    {
        private static readonly ILog log = LogManager.GetLogger(typeof(DocumentServices));
        /// <summary>
        /// Punto de entrada principal para la aplicación.
        /// </summary>
        [STAThread]
        static void Main()
        {
            using (Mutex mutex = new Mutex(false, "Menu", out bool isCreatedNew))
            {
                if (!isCreatedNew)
                {
                    Environment.Exit(0);
                }
            }

            log.Info("INICIA WRITE DOCUMENTS SERVICE");
            log4net.Config.XmlConfigurator.Configure();
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new DocumentServices());
        }
    }
}
