using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace ConsolaNetReader
{


 public class RequestEmail
    {
        
    [JsonPropertyName("campaignName")]
        public string CampaignName { get; set; }

        [JsonPropertyName("to")]
        public string To { get; set; }

        [JsonPropertyName("from")]
        public string From { get; set; }

        [JsonPropertyName("fromName")]
        public string FromName { get; set; }

        [JsonPropertyName("replyTo")]
        public string ReplyTo { get; set; }

        [JsonPropertyName("subject")]
        public string Subject { get; set; }

        [JsonPropertyName("textEmail")]
        public string TextEmail { get; set; }

        [JsonPropertyName("htmlEmail")]
        public string HtmlEmail { get; set; }

        [JsonPropertyName("references")]
        public string References { get; set; }

        [JsonPropertyName("auxiliaryField")]
        public string AuxiliaryField { get; set; }

        [JsonPropertyName("preloadedFileId")]
        public string PreloadedFileId { get; set; }

        [JsonPropertyName("fileName")]
        public string FileName { get; set; }



        [JsonPropertyName("removePreloadedFile")]
        public bool? RemovePreloadedFile { get; set; }

        [JsonPropertyName("selectAttachments")]
        public bool? SelectAttachments { get; set; }

        [JsonPropertyName("includeEmbedImage")]
        public bool? IncludeEmbedImage { get; set; }

        [JsonPropertyName("sendWithoutAttachedFiles")]
        public bool? SendWithoutAttachedFiles { get; set; }

        [JsonPropertyName("blackLists")]
        public string BlackLists { get; set; }
    }
}



