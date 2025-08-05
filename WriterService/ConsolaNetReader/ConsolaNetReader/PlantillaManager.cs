using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsolaNetReader
{
    public  class PlantillaManager
    {
        private  byte[] _contenidoPlantilla;

        public  void Inicializar(string rutaOriginal)
        {
            if (_contenidoPlantilla == null)
                _contenidoPlantilla = File.ReadAllBytes(rutaOriginal); // Solo una vez
        }

        public  string CrearCopiaTemporal()
        {
            string rutaTemp = Path.Combine(Path.GetTempPath(), $"plantilla_{Guid.NewGuid()}.docx");
            File.WriteAllBytes(rutaTemp, _contenidoPlantilla);
            return rutaTemp;
        }
    }
}
