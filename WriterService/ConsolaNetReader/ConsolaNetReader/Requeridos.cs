using MigraDoc.DocumentObjectModel;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsolaNetReader
{
    public static class Requeridos
    {
        public static int cliente;
        public static string rutaPlantillaMail= ConfigurationManager.AppSettings["mailPlantilla"];
        public static string plantillaMail;

        public static string getPlantillaMail() {

            if (plantillaMail.IsValueNullOrEmpty()) {

                plantillaMail=File.ReadAllText(rutaPlantillaMail);  
            }
            return plantillaMail;

        }



        static List<string> valoresContrato = new List<string>
        {
            "$$legal$$",
            "$$contrato$$",
            "$$nombre$$",
            "$$cuenta$$",
            "$$clabe$$",
            "$$correo$$",
            "$$id_ente$$",
            "$$firma$$",
            "$$dates$$"
        };

        static List<string> valoresGenerales = new List<string>
        {
            "$$nombrecompleto$$",
            "$$fechanacimiento$$",
            "$$genero$$",
            "$$entidadnac$$",
            "$$ocupacion$$",
            "$$correoelectronico$$",
           // "$$rfc$$",
            "$$geolocalizacion$$",
            "$$domiciliocompleto$$",
            "$$latitud$$",
            "$$longitud$$"
        };


    

    public static bool validarValor(string llave,string value ,string type) {

        bool validacion = true;


            try
            {

                if (type.Equals("DATOS CLIENTE"))
                {
                    if (valoresGenerales.Contains(llave))
                    {
                        if (value.IsValueNullOrEmpty())
                        {
                           // validacion = false;
                        }
                    }
                }
                else if (type.Equals("CONTRATO"))
                {
                    if (valoresContrato.Contains(llave))
                    {
                        if (value.IsValueNullOrEmpty())
                        {
                            validacion = true;
                        }

                    }
                }
            }catch(Exception xe)
            {

                throw xe;

            }

        return validacion;


    }

}
}
