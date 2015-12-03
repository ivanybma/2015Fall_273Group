package com.spring.Controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.WebResult;
import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebEndpoint;

import org.bson.BasicBSONObject;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.operation.bootstrap_server;
import com.mongodb.operation.group_operation;
import com.mongodb.operation.register_server;
import com.spring.Entity.BootstrapMsg;
import com.spring.Entity.BootstrapRsp;
import com.spring.Entity.DeviceGroupSegment;
import com.spring.Entity.GroupInfo;
import com.spring.Entity.GroupMsgRtv;
import com.spring.Entity.GroupRequest;
import com.spring.Entity.Msg;
import com.spring.Entity.ObjRscDesMap;
import com.spring.Entity.RegisterRsp;

@Controller
//@RestController  ---------> this is to define the whole class to be rendered as json
public class jsonController {

	
	ArrayList<String> notify = new ArrayList<String>();
	
	@RequestMapping(value="/bootstrap",method= RequestMethod.POST)
	@ResponseBody
//	public BootstrapRsp bootstrap_request(@RequestBody BootstrapMsg evd){
	public BootstrapRsp bootstrap_request(@RequestParam(value="endpoint_client_name", defaultValue="default")String endpoint_client_name
			,HttpServletRequest request){
		System.out.println("bootstrap");
//		System.out.println(headers.toString());		
		
		ObjectMapper mapper = new ObjectMapper();
        bootstrap_server bt = new bootstrap_server();
		
        BasicDBObject fields = new BasicDBObject().append("_id", 0);
        
		Document doc = bt.find(endpoint_client_name,fields);

		BootstrapRsp reply = new BootstrapRsp();
		
		
		try {
			reply = mapper.readValue(doc.toJson(), BootstrapRsp.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		List<String> test = reply.getRegister_server_uri();
		for(String x:test)
		{
		System.out.println(x);
		}
		reply.setBootstrap_time_stamp((new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())).toString());
		System.out.println(reply.getBootstrap_time_stamp());
		bt.update(reply);
		System.out.println("***Bootstrap response messge from Boostrap server to client:  ");
		System.out.println(doc.toJson());
		return reply;
	}
	
	@RequestMapping(value="/encrypt/register",method= RequestMethod.POST)
	@ResponseBody
//	public RegisterRsp register_request(@RequestBody RegisterMsg evd){
	public RegisterRsp register_request(@RequestBody String evd,HttpServletRequest request){
		
		String client_ip = "http://"+request.getRemoteAddr()+":"+request.getRemotePort();
		
		
		RegisterRsp reply = new RegisterRsp();
		System.out.println("*****register from client******");
		System.out.println(evd);
		register_server rt = new register_server();
		
		Document myDoc = Document.parse(evd);
		String epn = myDoc.get("endpoint_client_name").toString();
		
		rt.insert(evd);
		Document ipDoc = new Document("device_ip_port",client_ip);
		rt.update(epn, ipDoc);
		reply.setEndpoint_client_name(myDoc.get("endpoint_client_name").toString());
		reply.setReturn_code(100);

		
		return reply;
	}
	
	@RequestMapping(value="/encrypt/register/deregister",method= RequestMethod.POST)
	@ResponseBody
	public String deregister(@RequestParam(value="endpoint_client_name", defaultValue="default")String endpoint_client_name){
		String reply="";
		System.out.println("****de-register from client****");
		System.out.println(endpoint_client_name);
		register_server rt = new register_server();
		Document degDoc = new Document("endpoint_client_name",endpoint_client_name);
		rt.dereg(degDoc);
		reply="100";
		return reply;
	}
	
	@RequestMapping(value="/encrypt/register/update",method= RequestMethod.POST)
	@ResponseBody
	public String update(@RequestBody String upd){
//		System.out.println("id="+evd.getId()+" product id= "+evd.getProduct_id()+ " with new seq"+evd.getNewseq()+ " locates at "+evd.getLatitude()+" - "+evd.getLongitude());

		System.out.println("*****update from client*********");
		System.out.println(upd);
		String reply = "";
		
		register_server rt = new register_server();
		
		Document myDoc = Document.parse(upd);
		String epn = myDoc.get("endpoint_client_name").toString();

		myDoc.remove("endpoint_client_name");

		//Document rsDoc = new Document("object_list",myDoc.toJson());
		rt.update(epn, myDoc);

		reply="100";		
		return reply;
	}
	
	@RequestMapping(value="/encrypt/register/singleupdate",method= RequestMethod.POST)
	@ResponseBody
	public String singleupdate(@RequestBody String upd){
		System.out.println("*****single update from client*********");
		System.out.println(upd);
		String reply = "";
		
		register_server rt = new register_server();
		
		Document myDoc = Document.parse(upd);
		String epn = myDoc.get("endpoint_client_name").toString();
		String rsc_dtl = myDoc.getString("src_detail").toString();
		String[] rsc_helper = rsc_dtl.split(":");
		String src_path = rsc_dtl.substring(0,rsc_dtl.indexOf(":"));
		String src_val = rsc_dtl.substring(rsc_dtl.indexOf(":")+1);
		System.out.println("single update for: " +rsc_dtl);
		rt.singleupdate(epn, src_path, src_val);
		
		notify.add(rsc_dtl);
		
		return "single update done";
	}
    
    @RequestMapping(value="/getnotify",method= RequestMethod.GET)
    @ResponseBody
    public String getnotify(){

    	ArrayList<String> tmp=new ArrayList<String>(notify); 
    	notify.clear();
    	
			ObjectMapper mapper = new ObjectMapper();
			String output=null;
			try {
				output = mapper.writeValueAsString(tmp);
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}

    	
		return output;
    	
    }
	
	
    @RequestMapping(value = "/reply")
    public String getAliveResponseJSON(Model model)
    {
    	System.out.println("server");

		
		BootstrapMsg bmsg = new BootstrapMsg();
		bmsg.setEndpoint_client_name("urn:uuid:00000001-0001-0001-000000000001");
		
        return "jsonTemplate";
    }
    
    @RequestMapping(value = "/controlpanel")
    public String controlpanel()
    {
    	System.out.println("control panel test");

        return "control_panel";
    }
    
    @RequestMapping(value="/manage_device/{devid}",method= RequestMethod.GET)
    public String manage_device(@PathVariable("devid") String devid, ModelMap model){

    	 model.addAttribute("devid", devid);
    	
    	
    	BasicDBObject fields = new BasicDBObject().append("_id", 0);
    	register_server bt = new register_server();
		ObjectMapper mapper = new ObjectMapper();
		
    	
    	Document btdoc = bt.find(devid,fields);
        
		Document objlst = (Document)btdoc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		Document objistsrcgp = (Document)objistsrclst.get("11");
		
		if(objistsrcgp==null)
			return "manage_device";
		
		DeviceGroupSegment dv_gp = new DeviceGroupSegment();
    	
		try {
			dv_gp = mapper.readValue(objistsrcgp.toJson(), DeviceGroupSegment.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}

		String group_info="";
		try {
			group_info = mapper.writeValueAsString(dv_gp);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		model.addAttribute("group_info", group_info);
		
		group_operation gp = new group_operation();
		Iterator<Document> gpdocs = gp.find_by_owner(devid,fields);
		ArrayList<String> gparray = new ArrayList<String>();
		
		while(gpdocs.hasNext()){	
			gparray.add(gpdocs.next().get("groupname").toString());
		}
		
		String gplst=null;
		try {
			gplst = mapper.writeValueAsString(gparray);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		model.addAttribute("gplst", gplst);
		
    	
        return "manage_device";
    }
    
    @RequestMapping(value = "/gpdvlst/{gpname}",method= RequestMethod.GET)
    @ResponseBody
    public String getgpdvlst(@PathVariable("gpname") String gpname)
    {
    	GroupInfo reply = new GroupInfo();
		group_operation gp = new group_operation();
		BasicDBObject fields = new BasicDBObject().append("_id", 0);
		ObjectMapper mapper = new ObjectMapper();
        
		Document gpdoc = gp.find(gpname,fields);

		try {
			reply = mapper.readValue(gpdoc.toJson(), GroupInfo.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}

    	ArrayList<String> rstarray = new ArrayList<String>(reply.getDevices());
    	
		String output=null;
		try {
			output = mapper.writeValueAsString(rstarray);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
        return output;
    }
    
    
    @RequestMapping(value = "/getobjrsc",method= RequestMethod.GET)
    @ResponseBody
    public String getobjrsc()
    {
    	ArrayList<String> rstarray = new ArrayList<String>();
    	for (Map.Entry<String, String> entry : ObjRscDesMap.descHelper.entrySet())
   		{
    		rstarray.add(entry.getKey() + "!" + entry.getValue());
 		}
    	
		ObjectMapper mapper = new ObjectMapper();
		String output=null;
		try {
			output = mapper.writeValueAsString(rstarray);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}

		System.out.println(output);
        return output;
    }
    
    @RequestMapping(value = "/readclient/{devid}/{srcpath}",method= RequestMethod.GET)
    @ResponseBody
    public String readclient(@PathVariable("srcpath") String srcpath,@PathVariable("devid") String devid)
    {
    	System.out.println(devid+ " " + srcpath);
    	srcpath = srcpath.replace("*", "/");
    	
    	System.out.println(srcpath);
    	register_server bt = new register_server();
		
        BasicDBObject fields = new BasicDBObject().append("_id", 0);
        
		Document doc = bt.find(devid,fields);
		Document objlst = (Document)doc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		String objistsrcval = objistsrclst.get("4").toString();
		
		System.out.println(objistsrcval);
		objistsrcval=objistsrcval+"rslst/"+srcpath;
		
		RestTemplate restTemplate = new RestTemplate();	
		String output = restTemplate.getForObject(objistsrcval, String.class);
    	return output;
    }
    
    @RequestMapping(value = "/discoverclient/{devid}/{srcpath}",method= RequestMethod.GET)
    @ResponseBody
    public String discoverclient(@PathVariable("srcpath") String srcpath,@PathVariable("devid") String devid)
    {
    	System.out.println(devid+ " " + srcpath);
    	srcpath = srcpath.replace("*", "/");
    	
    	System.out.println(srcpath);
    	register_server bt = new register_server();
		
        BasicDBObject fields = new BasicDBObject().append("_id", 0);
        
		Document doc = bt.find(devid,fields);
		Document objlst = (Document)doc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		String objistsrcval = objistsrclst.get("4").toString();
		
		System.out.println(objistsrcval);
		objistsrcval=objistsrcval+"discover/"+srcpath;
		
		RestTemplate restTemplate = new RestTemplate();	
		String output = restTemplate.getForObject(objistsrcval, String.class);
    	return output;
    }
    
    @RequestMapping(value = "/writeclient/{devid}/{srcpath}/{rscval}",method= RequestMethod.GET)
    @ResponseBody
    public String writeclient(@PathVariable("srcpath") String srcpath,@PathVariable("devid") String devid,
    		@PathVariable("rscval") String rscval)
    {
    	System.out.println(devid+ " " + srcpath);
    	srcpath = srcpath.replace("*", "/");
    	
    	System.out.println(srcpath);
    	register_server bt = new register_server();
		
        BasicDBObject fields = new BasicDBObject().append("_id", 0);
        
		Document doc = bt.find(devid,fields);
		Document objlst = (Document)doc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		String objistsrcval = objistsrclst.get("4").toString();
		
		System.out.println(objistsrcval);
		objistsrcval=objistsrcval+"write/"+srcpath+"/"+rscval;
		
		RestTemplate restTemplate = new RestTemplate();	
		String output = restTemplate.getForObject(objistsrcval, String.class);
    	return output;
    }
    
    @RequestMapping(value = "/writeattclient/{devid}/{attpath}/{att}/{attv}",method= RequestMethod.GET)
    @ResponseBody
    public String writeattclient(@PathVariable("attpath") String attpath,@PathVariable("devid") String devid,
    		@PathVariable("att") String att,@PathVariable("attv") String attv)
    {
    	System.out.println(devid+ " " + attpath);
    	attpath = attpath.replace("*", "/");
    	
    	System.out.println(attpath);
    	register_server bt = new register_server();
		
        BasicDBObject fields = new BasicDBObject().append("_id", 0);
        
		Document doc = bt.find(devid,fields);
		Document objlst = (Document)doc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		String objistsrcval = objistsrclst.get("4").toString();
		
		System.out.println(objistsrcval);
		objistsrcval=objistsrcval+"writeattr/"+attpath+"/"+att + "/" + attv;
		
		RestTemplate restTemplate = new RestTemplate();	
		String output = restTemplate.getForObject(objistsrcval, String.class);
    	return output;
    }
    
    @RequestMapping(value = "/executeclient/{devid}/{rscpath}/{cmd}",method= RequestMethod.GET)
    @ResponseBody
    public String executeclient(@PathVariable("rscpath") String rscpath,@PathVariable("devid") String devid,
    		@PathVariable("cmd") String cmd)
    {
    	System.out.println(devid+ " " + rscpath);
    	rscpath = rscpath.replace("*", "/");
    	
    	System.out.println(rscpath);
    	register_server bt = new register_server();
		
        BasicDBObject fields = new BasicDBObject().append("_id", 0);
        
		Document doc = bt.find(devid,fields);
		Document objlst = (Document)doc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		String objistsrcval = objistsrclst.get("4").toString();
		
		System.out.println(objistsrcval);
		objistsrcval=objistsrcval+"execute/"+rscpath+"/"+cmd;
		
		RestTemplate restTemplate = new RestTemplate();	
		String output = restTemplate.getForObject(objistsrcval, String.class);
    	return output;
    }
    
    @RequestMapping(value = "/createinstanceclient/{devid}/{srcpath}/{rscval}",method= RequestMethod.GET)
    @ResponseBody
    public String createinstanceclient(@PathVariable("srcpath") String srcpath,@PathVariable("devid") String devid,
    		@PathVariable("rscval") String rscval)
    {
    	System.out.println(devid+ " " + srcpath);
    	srcpath = srcpath.replace("*", "/");
    	
    	System.out.println(srcpath);
    	register_server bt = new register_server();
		
        BasicDBObject fields = new BasicDBObject().append("_id", 0);
        
		Document doc = bt.find(devid,fields);
		Document objlst = (Document)doc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		String objistsrcval = objistsrclst.get("4").toString();
		
		System.out.println(objistsrcval);
		objistsrcval=objistsrcval+"createinstance/"+srcpath+"/"+rscval;
		
		RestTemplate restTemplate = new RestTemplate();	
		String output = restTemplate.getForObject(objistsrcval, String.class);
    	return output;
    }
    
    
    @RequestMapping(value = "/deleteinstanceclient/{devid}/{srcpath}",method= RequestMethod.GET)
    @ResponseBody
    public String deleteinstanceclient(@PathVariable("srcpath") String srcpath,@PathVariable("devid") String devid)
    {
    	System.out.println(devid+ " " + srcpath);
    	srcpath = srcpath.replace("*", "/");
    	
    	System.out.println(srcpath);
    	register_server bt = new register_server();
		
        BasicDBObject fields = new BasicDBObject().append("_id", 0);
        
		Document doc = bt.find(devid,fields);
		Document objlst = (Document)doc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		String objistsrcval = objistsrclst.get("4").toString();
		
		System.out.println(objistsrcval);
		objistsrcval=objistsrcval+"deleteinst/"+srcpath;
		
		RestTemplate restTemplate = new RestTemplate();	
		String output = restTemplate.getForObject(objistsrcval, String.class);
    	return output;
    }
    
	
	@RequestMapping(value="/changeactivegp/{devid}/{actgp}",method= RequestMethod.GET)
	@ResponseBody
	public String changeactivegp(@PathVariable("devid") String devid, @PathVariable("actgp") String actgp){
		
		group_operation gpopr = new group_operation();

		String src_path = "object_list.4.0.11.active";
		gpopr.singleupdate(devid, src_path, actgp);
		
		clientgroupsync(devid);
		
		return "single update done";
	}

	@RequestMapping(value="/encrypt/register/newmsg/{devid}/{newmsg}/{datetime}",method= RequestMethod.GET)
	@ResponseBody
	public String newmsg(@PathVariable("devid") String devid, @PathVariable("newmsg") String newmsg,
			 @PathVariable("datetime") String datetime){
		
		System.out.println("msg is now in register server");
		BasicDBObject fields = new BasicDBObject().append("_id", 0);
		register_server bt = new register_server();
    	Document btdoc = bt.find(devid,fields);
    	ObjectMapper mapper = new ObjectMapper();
        
		Document objlst = (Document)btdoc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		Document objistsrcgp = (Document)objistsrclst.get("11");
		DeviceGroupSegment dv_gp = new DeviceGroupSegment();
    	
		try {
			dv_gp = mapper.readValue(objistsrcgp.toJson(), DeviceGroupSegment.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		String active_group = dv_gp.getActive();
		
		if(active_group==null || active_group.equals("")|| active_group.equals("disabled"))
			return "";
		
		group_operation gpopr = new group_operation();

		DBObject document1 = new BasicDBObject();
		
		List<DBObject> documents = (List<DBObject>) gpopr.find(active_group,fields).get("msglist");
		if(documents==null)
			documents= new ArrayList<DBObject>();
		
		document1.put("sender", devid);
		document1.put("content", newmsg);
		document1.put("datetime", datetime);

		documents.add(document1);
		
		gpopr.gpmsglistupdate(active_group, "msglist", documents);
		
		return "message sent";
	}
	

	@RequestMapping(value="/encrypt/register/rtvgpmsg/{devid}",method= RequestMethod.GET)
	@ResponseBody
	public String rtvgpmsg(@PathVariable("devid") String devid){
		
		BasicDBObject fields = new BasicDBObject().append("_id", 0);
		register_server bt = new register_server();
    	Document btdoc = bt.find(devid,fields);
    	ObjectMapper mapper = new ObjectMapper();
        
		Document objlst = (Document)btdoc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		Document objistsrcgp = (Document)objistsrclst.get("11");
		DeviceGroupSegment dv_gp = new DeviceGroupSegment();
    	
		try {
			dv_gp = mapper.readValue(objistsrcgp.toJson(), DeviceGroupSegment.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		String active_group = dv_gp.getActive();
		
		if(active_group==null || active_group.equals("")|| active_group.equals("disabled"))
			return "";
		//System.out.println(active_group);
		group_operation gpopr = new group_operation();

		Document documents = (Document) gpopr.find(active_group,fields);
		GroupInfo gp_dtl=new GroupInfo();
		try {
			gp_dtl = mapper.readValue(documents.toJson(), GroupInfo.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		GroupMsgRtv gpmsgrtv = new GroupMsgRtv();
		gpmsgrtv.setMsglist(gp_dtl.getMsglist());
		gpmsgrtv.setGroupname(active_group);

		
		String output=null;
		try {
			output = mapper.writeValueAsString(gpmsgrtv);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}

	return output;
	}
	
	
	
	@RequestMapping(value="/adddevice/{devid}/{gpname}/{newdevice}",method= RequestMethod.GET)
	@ResponseBody
	public String adddevice(@PathVariable("devid") String devid, @PathVariable("gpname") String gpname,
			@PathVariable("newdevice") String newdevice){
		group_operation gpopr = new group_operation();

    	BasicDBObject fields = new BasicDBObject().append("_id", 0);
    	register_server bt = new register_server();
		ObjectMapper mapper = new ObjectMapper();
		
    	Document btdoc = bt.find(newdevice,fields);
    	
        if(btdoc==null)
        	return "new device not found";
        
		Document objlst = (Document)btdoc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		Document objistsrcgp = (Document)objistsrclst.get("11");
		
		DeviceGroupSegment dv_gp = new DeviceGroupSegment();
		List<GroupRequest> gprstlist = new ArrayList<GroupRequest>();

		try {
			dv_gp = mapper.readValue(objistsrcgp.toJson(), DeviceGroupSegment.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}

		gprstlist = dv_gp.getGprst();
		List<DBObject> documents = new ArrayList<>();
		DBObject document1 = new BasicDBObject();
// to add a new group request to the existing list of document, I need tor etrieve all of them and use them to
		//prepare the documents list, and then add my new one to it, and then update the collection(in fact, this is
		//cumbersome, but I still not yet get any better solution to avoid this dump action
		for(int i=0; i<gprstlist.size(); i++)
		{
			document1 = new BasicDBObject();
			
			document1.put("from", gprstlist.get(i).getFrom());
			document1.put("group", gprstlist.get(i).getGroup());
			document1.put("status", gprstlist.get(i).getStatus());
			
			documents.add(document1);
		}
		
		document1 = new BasicDBObject();
		document1.put("from", devid);
		document1.put("group", gpname);
		document1.put("status", "");
		documents.add(document1);
		
		gpopr.gprstlistupdate(newdevice, "object_list.4.0.11.gprst", documents);
		
		return "Join group invitation sent";
	}
	
	
    public String clientgroupsync(String devid)
    {

    	register_server bt = new register_server();
		
        BasicDBObject fields = new BasicDBObject().append("_id", 0);
        
		Document doc = bt.find(devid,fields);
		Document objlst = (Document)doc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		String objistsrcval = objistsrclst.get("4").toString();
		
		Document gpdoc = (Document)objistsrclst.get("11");
		String gpdtl=gpdoc.toJson();
		System.out.println("before to json"+ gpdoc.toJson());
		objistsrcval=objistsrcval+"updategroup";
		System.out.println(objistsrcval);
		RestTemplate restTemplate = new RestTemplate();	
		//restTemplate.postForObject( reg_uri, full_rsc, RegisterRsp.class);
		String reply = restTemplate.postForObject(objistsrcval, gpdtl, String.class);
		System.out.println(reply);
    	return reply;
    }
	
	
	
	@RequestMapping(value="/newgroup/{devid}/{gpname}",method= RequestMethod.GET)
	@ResponseBody
	public String newgroup(@PathVariable("devid") String devid, @PathVariable("gpname") String gpname){
		
		group_operation gpopr = new group_operation();
		

    	BasicDBObject fields = new BasicDBObject().append("_id", 0);
    	register_server bt = new register_server();
		ObjectMapper mapper = new ObjectMapper();
		
    	
    	Document btdoc = bt.find(devid,fields);
        
		Document objlst = (Document)btdoc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		Document objistsrcgp = (Document)objistsrclst.get("11");
		DeviceGroupSegment dv_gp = new DeviceGroupSegment();
    	
		try {
			dv_gp = mapper.readValue(objistsrcgp.toJson(), DeviceGroupSegment.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		List<String> newgp_lst = dv_gp.getList();
		newgp_lst.add(gpname);
		
		String src_path = "object_list.4.0.11.list";
		gpopr.listupdate(devid, src_path, newgp_lst);
		
		
		
		GroupInfo newgp = new GroupInfo();
		List<String> devicelst = new ArrayList<String>();
		devicelst.add(devid);
		newgp.setDevices(devicelst);
		newgp.setEndpoint_client_name(devid);
		newgp.setGroupname(gpname);
		
		String gpdtl=null;
		try {
			gpdtl = mapper.writeValueAsString(newgp);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		
		gpopr.newgroup(gpname, gpdtl);
		clientgroupsync(devid);
		
		return "single update done";
	}
	

	@RequestMapping(value="/deletegroup/{devid}/{gpname}",method= RequestMethod.GET)
	@ResponseBody
	public String deletegroup(@PathVariable("devid") String devid, @PathVariable("gpname") String gpname){
		
		group_operation gpopr = new group_operation();
    	GroupInfo reply = new GroupInfo();

		BasicDBObject fields = new BasicDBObject().append("_id", 0);
		ObjectMapper mapper = new ObjectMapper();
        
		Document gpdoc = gpopr.find(gpname,fields);

		try {
			reply = mapper.readValue(gpdoc.toJson(), GroupInfo.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}

    	ArrayList<String> rstarray = new ArrayList<String>(reply.getDevices());
    	
    	//loop and update all the devices under this group
    	for(int dev=0; dev<rstarray.size(); dev++)
    	{
    	register_server bt = new register_server();
    	Document btdoc = bt.find(rstarray.get(dev).toString(),fields);
        
		Document objlst = (Document)btdoc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		Document objistsrcgp = (Document)objistsrclst.get("11");
		DeviceGroupSegment dv_gp = new DeviceGroupSegment();
    	
		try {
			dv_gp = mapper.readValue(objistsrcgp.toJson(), DeviceGroupSegment.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		List<String> gp_lst = dv_gp.getList();
		for(int i=0; i<gp_lst.size(); i++)
		{
			if(gp_lst.get(i).equals(gpname))
				{
					gp_lst.remove(i);
					break;
				}
		}
		
		if(dv_gp.getActive().equals(gpname))
			dv_gp.setActive("");
		
		String gpdtl=null;
		try {
			gpdtl = mapper.writeValueAsString(dv_gp);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		
		String src_path = "object_list.4.0.11";
		gpopr.gpwholeupdate(rstarray.get(dev).toString(), src_path, gpdtl);
		
		clientgroupsync(rstarray.get(dev).toString());
    	}

		//**after devices' group list info, here delete the group info from register group info collection
		
		gpopr.deleterggroup(gpname);
		
		
		
		return "group deleted";
	}
	
	@RequestMapping(value="/deletedevice/{gpowner}/{gpname}/{devid}",method= RequestMethod.GET)
	@ResponseBody
	public String deletedevice(@PathVariable("gpowner") String gpowner, 
			@PathVariable("gpname") String gpname,@PathVariable("devid") String devid){
		
		group_operation gpopr = new group_operation();
    	GroupInfo reply = new GroupInfo();

		BasicDBObject fields = new BasicDBObject().append("_id", 0);
		ObjectMapper mapper = new ObjectMapper();
    	
    	//update the deleted device group list

    	register_server bt = new register_server();
    	Document btdoc = bt.find(devid,fields);
        
		Document objlst = (Document)btdoc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		Document objistsrcgp = (Document)objistsrclst.get("11");
		DeviceGroupSegment dv_gp = new DeviceGroupSegment();
    	
		try {
			dv_gp = mapper.readValue(objistsrcgp.toJson(), DeviceGroupSegment.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		List<String> gp_lst = dv_gp.getList();
		for(int i=0; i<gp_lst.size(); i++)
		{
			if(gp_lst.get(i).equals(gpname))
				{
					gp_lst.remove(i);
					break;
				}
		}
		
		if(dv_gp.getActive().equals(gpname))
			dv_gp.setActive("");
		
		String gpdtl=null;
		try {
			gpdtl = mapper.writeValueAsString(dv_gp);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		
		String src_path = "object_list.4.0.11";
		gpopr.gpwholeupdate(devid, src_path, gpdtl);
		
		clientgroupsync(devid);

		//**after devices' group list info, here update the group info from register group info collection
		
		Document gpdoc = gpopr.find(gpname,fields);

		try {
			reply = mapper.readValue(gpdoc.toJson(), GroupInfo.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		ArrayList<String> rstarray = new ArrayList<String>(reply.getDevices());
		
		for(int i = 0; i<rstarray.size(); i++){
			
			if(rstarray.get(i).equals(devid)){
				rstarray.remove(i);
				break;
			}
		}
		
		gpopr.updategroup(gpname, rstarray);
		
		return "device deleted";
	}
	
	@RequestMapping(value="/rejectgroup/{devid}/{gpname}/{fromdevid}",method= RequestMethod.GET)
	@ResponseBody
	public String rejectgroup(@PathVariable("devid") String devid, @PathVariable("gpname") String gpname,
			@PathVariable("fromdevid") String fromdevid){
		
		group_operation gpopr = new group_operation();
		

    	BasicDBObject fields = new BasicDBObject().append("_id", 0);
    	register_server bt = new register_server();
		ObjectMapper mapper = new ObjectMapper();
		
    	//retrieve the current group list for the device and then add the group to it's group list
    	Document btdoc = bt.find(devid,fields);
        
		Document objlst = (Document)btdoc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		Document objistsrcgp = (Document)objistsrclst.get("11");
		
		DeviceGroupSegment dv_gp = new DeviceGroupSegment();
		
		try {
			dv_gp = mapper.readValue(objistsrcgp.toJson(), DeviceGroupSegment.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		List<GroupRequest> gprstlist = new ArrayList<GroupRequest>();
		gprstlist = dv_gp.getGprst();
		
		List<DBObject> documents = new ArrayList<>();
		DBObject document1 = new BasicDBObject();
		for(int i=0; i<gprstlist.size(); i++)
		{
			document1 = new BasicDBObject();
			
			if(gprstlist.get(i).getFrom().equals(fromdevid)&&gprstlist.get(i).getGroup().equals(gpname)&&gprstlist.get(i).getStatus().equals(""))
				gprstlist.get(i).setStatus("rejected");
			document1.put("from", gprstlist.get(i).getFrom());
			document1.put("group", gprstlist.get(i).getGroup());
			document1.put("status", gprstlist.get(i).getStatus());
			
			documents.add(document1);
		}

		gpopr.gprstlistupdate(devid, "object_list.4.0.11.gprst", documents);
		
		return "Group rejected";
	}
	
	
	@RequestMapping(value="/acceptgroup/{devid}/{gpname}/{fromdevid}",method= RequestMethod.GET)
	@ResponseBody
	public String acceptgroup(@PathVariable("devid") String devid, @PathVariable("gpname") String gpname,
			@PathVariable("fromdevid") String fromdevid){
		
		group_operation gpopr = new group_operation();
		

    	BasicDBObject fields = new BasicDBObject().append("_id", 0);
    	register_server bt = new register_server();
		ObjectMapper mapper = new ObjectMapper();
		
    	//retrieve the current group list for the device and then add the group to it's group list
    	Document btdoc = bt.find(devid,fields);
        
		Document objlst = (Document)btdoc.get("object_list");
		Document objistlst = (Document)objlst.get("4");
		Document objistsrclst = (Document)objistlst.get("0");
		Document objistsrcgp = (Document)objistsrclst.get("11");
		
		DeviceGroupSegment dv_gp = new DeviceGroupSegment();
		List<GroupRequest> gprstlist = new ArrayList<GroupRequest>();
		
    	
		try {
			dv_gp = mapper.readValue(objistsrcgp.toJson(), DeviceGroupSegment.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		List<String> newgp_lst = dv_gp.getList();
		newgp_lst.add(gpname);
		
		String src_path = "object_list.4.0.11.list";
		gpopr.listupdate(devid, src_path, newgp_lst);
		
		gprstlist = dv_gp.getGprst();
		List<DBObject> documents = new ArrayList<>();
		DBObject document1 = new BasicDBObject();
		
		for(int i=0; i<gprstlist.size(); i++)
		{
			document1 = new BasicDBObject();
			
			if(gprstlist.get(i).getFrom().equals(fromdevid)&&gprstlist.get(i).getGroup().equals(gpname)&&gprstlist.get(i).getStatus().equals(""))
				gprstlist.get(i).setStatus("accepted");
			
			document1.put("from", gprstlist.get(i).getFrom());
			document1.put("group", gprstlist.get(i).getGroup());
			document1.put("status", gprstlist.get(i).getStatus());
			
			documents.add(document1);
		}
		gpopr.gprstlistupdate(devid, "object_list.4.0.11.gprst", documents);
		
		//update group control device list for the new joined device
		GroupInfo reply = new GroupInfo();
		
		Document gpdoc = gpopr.find(gpname,fields);

		try {
			reply = mapper.readValue(gpdoc.toJson(), GroupInfo.class);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			System.out.println("JsonMappingException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		ArrayList<String> rstarray = new ArrayList<String>(reply.getDevices());
		
		rstarray.add(devid);
		
		gpopr.updategroup(gpname, rstarray);
	
		clientgroupsync(devid);

		return "Group joined";
	}
	
	
    
}
