using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsolaNetReader
{
    public static class RefreshToken
    {
        public static string token;
        public static int expire;
        public static DateTimeOffset expiraEn;


        public static bool MustBeeRefresh()
        {

            bool refresh = false;


            if (DateTimeOffset.UtcNow >= expiraEn)
            {
                 expiraEn = DateTimeOffset.UtcNow.AddSeconds(expire-1000);
                 refresh = true;
            }

            return refresh;
        }

    }
}
