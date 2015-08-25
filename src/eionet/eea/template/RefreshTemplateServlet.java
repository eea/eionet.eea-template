/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 2.0.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Jaanus Heinlaid, Tieto Eesti
 */
package eionet.eea.template;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Downloads EEA template snippets form http://www.eea.europa.eu template service and stores them as .html files into file system.
 * @author altnyris
 *
 */
public class RefreshTemplateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        updateTemplate("http://www.eea.europa.eu/templates/v2/getHeader", "header");
        updateTemplate("http://www.eea.europa.eu/templates/v2/getRequiredHead", "required_head");
        updateTemplate("http://www.eea.europa.eu/templates/v2/getFooter", "footer");

        res.sendRedirect("");
    }

    private boolean updateTemplate(String url, String page) {
        Pair<Integer, String> result = readContentFromUrl(url);

        if (result != null && result.getId() != null && result.getId() == 200 && StringUtils.isNotBlank(result.getValue())) {
            return saveToFile(page, result.getValue());
        }
        return false;
    }

    /**
     * Reads the URL and returns the content.
     * Pair id - HTTP status code, Pair value - response body.
     *
     * @param url url to check.
     * @return
     */
    private Pair<Integer, String> readContentFromUrl(String url) {
        Pair<Integer, String> result = null;

        if (StringUtils.isNotBlank(url)) {
            try {
                HttpClient client = new HttpClient();
                HttpMethod get = new GetMethod(url);

                client.executeMethod(get);
                result = new Pair<Integer, String>();
                result.setId(get.getStatusCode());
                if (get.getStatusCode() != 404) {
                    result.setValue(get.getResponseBodyAsString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    private boolean saveToFile(String page, String content) {
        try {
            String folderPath = getServletContext().getInitParameter("templateCacheFolder");
            
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                // creates the directory, including any necessary but nonexistent parent directories
                folder.mkdirs();
            }
            File filePath = new File(folder, page + ".html");
            if (filePath.exists() && filePath.isFile()) {
                filePath.delete();
            }

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(filePath);
                IOUtils.copy(new ByteArrayInputStream(content.getBytes("UTF-8")), outputStream);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
