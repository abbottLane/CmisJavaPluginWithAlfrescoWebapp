package com.springapp.mvc.Controllers;

import com.springapp.mvc.models.QueryModel;
import com.springapp.mvc.service.CmisSessionService;
import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import edu.byu.oit.core.cmis.CMISInterface.IObjectID;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Controller
public class FilterByTypeController {

    @Resource(lookup="cmis")
    CmisSessionService sessionService;
    CMISSessionInterface session;

    @RequestMapping(value="/filterByType", method = RequestMethod.GET)
    public ModelAndView loadPage(ModelMap model) {
        model.addAttribute("environment", sessionService.getEnvironment());
        model.addAttribute("user", sessionService.getUsername());
        return new ModelAndView("filterByType", "command", new QueryModel());
    }

    @RequestMapping(value="/filterByType", method = RequestMethod.POST)
    public ModelAndView execute(@ModelAttribute("SpringWeb")QueryModel queryModel, ModelMap model){

        //ESTABLISH a connection to the repo
        session = sessionService.getSession();

        //FORM a query based on input from QueryModel, restricting query domain to the test folder
        String UserHome;
        if(sessionService.getUsername().equals("admin")){
            UserHome="abbott";
        }
        else{ UserHome=sessionService.getUsername();}
        IObjectID folderId = session.getObjectIdByPath("/User Homes/"+UserHome +"/");
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM cmis:document WHERE IN_TREE('"+folderId.toString()+"') AND cmis:contentStreamMimeType ");

        String fileName;
        if(queryModel.getFiletype().equals("image")){
            fileName="LIKE '%image%'";
            query.append(fileName);
        }
        else if(queryModel.getFiletype().equals("txt")){
            fileName="LIKE '%text%'";
            query.append(fileName);
        }
        else if(queryModel.getFiletype().equals("mp3")){
            fileName="LIKE '%audio%'";
            query.append(fileName);
        }
        else if(queryModel.getFiletype().equals("mp4")){
            fileName="LIKE '%video%'";
            query.append(fileName);
        }
        else if(queryModel.getFiletype().equals("other")){
            fileName="LIKE '%application%'";
            query.append(fileName);
        }

        //EXECUTE the query
        ItemIterable<QueryResult> results = session.executeQuery(query.toString());

        //TRANSFORM the results into urls
        //count number of results, store the ides in a List
        int resultsNum=0;
        List<Document> queryResults = new ArrayList<Document>();
        List<String> docNames = new ArrayList<String>();
        for (QueryResult item :results) {
            resultsNum++;
            String docId =(String)item.getPropertyByQueryName("cmis:objectId").getFirstValue();
            Document doc = session.getDocument(docId);
            queryResults.add(doc);
            docNames.add(doc.getName());
        }

        //form a list of thumbnails with the query results from the List of queryResultIds
        List<String>thumbUrls= formUrlList(queryResults, "thumb");
        List<String>fullUrls = formUrlList(queryResults, "full");

        //push all relevent information to the ModelMap
        model.addAttribute("thumbUrlList",thumbUrls);
        model.addAttribute("fullUrlList", fullUrls);
        model.addAttribute("numberOfResults", resultsNum);
        model.addAttribute("documentNames", docNames);
        model.addAttribute("environment", sessionService.getEnvironment());
        model.addAttribute("user", sessionService.getUsername());

        //Return the model and view
        return new ModelAndView("filterByType", "command", new QueryModel());
    }

    private List<String> formUrlList(List<Document> queryResults, String type) {
        List<String>thumbUrls= new ArrayList<String>();
        List<String>fullUrls=new ArrayList<String>();

        for(Document doc : queryResults){
            String thumbUrl;
            if(session.doesAlfrescoThumbnailExist(doc)) {

                thumbUrl = session.getAlfrescoThumbnailUrl(doc);
            }
            else{ // allow for custom thumbnail images
                if(doc.getContentStreamMimeType().contains("text")) {
                    thumbUrl = "./resources/additionalResources/customThumbImages/docThumb.png";
                }
                else if(doc.getContentStreamMimeType().contains("audio")){
                    thumbUrl= "./resources/additionalResources/customThumbImages/musicThumb.png";
                }
                else if (doc.getContentStreamMimeType().contains("video")){
                    thumbUrl="./resources/additionalResources/customThumbImages/videoThumb.png";
                }
                else{
                    thumbUrl = "./resources/additionalResources/customThumbImages/docThumb.png";
                }
            }
            String fullUrl = session.getAlfrescoDocumentUrl(doc);

            thumbUrls.add(thumbUrl);
            fullUrls.add(fullUrl);
        }

        if(type.equals("full")){
            return fullUrls;
        }
        else if(type.equals("thumb")){
            return thumbUrls;
        }

        //Else return the thumbnail urls, but this never gets called anyways
        return thumbUrls;
    }
}

