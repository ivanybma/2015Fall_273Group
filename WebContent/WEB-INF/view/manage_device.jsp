<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js" type="text/javascript"></script> 
<title>Device Panel</title>

<style>

#notify_result_table {
width: 500px;
}
#group_table{
height: 1520px;
width: 550px;
}
#div_layout{
width: 650px;
}
#group_layout {
width: 550px;
}

#group_device_list, #Request_list{
width: 330px;
}



.notify_txt {
width: 400px;
}

#multiple_list{
width: 150px;
}

div.rst_div {
    width: 630px;
    height: 100px;
    border: 3px solid #73AD21;
    overflow: scroll;
}

#device_list_div {
    width: 350px;
    height: 150px;
    border: 3px solid #73AD21;
    overflow: scroll;
}

#request_list_div{
    width: 350px;
    height: 100px;
    border: 1px solid #73AD21;
    overflow: scroll;
}

button{
	width:120px;
}

select{
	width:120px;
}

table {
border: 1px solid black;
}

#testid{
border: 1px solid red;
}

td{
border: 1px solid #0A1200;
vertical-align:top;
}
</style>

<script>
$(document).ready(function(){

	$("#delete_group").prop("disabled",true);
	$("#add_device").prop("disabled",true);
	
	$("#Read").click(function(){
		
		var rscpth = $("#read_obj_id :selected").val();
		
		if($("#read_obj_ist_id :selected").text()!="Null")
			rscpth = rscpth +"*" + $("#read_obj_ist_id").val();
		if($("#read_obj_ist_id :selected").text()=="Null"&&$("#read_rsc_id :selected").text()!="Null")
			rscpth = rscpth +"*" + "0";
		if($("#read_rsc_id :selected").text()!="Null")	
			rscpth = rscpth +"*" + $("#read_rsc_id :selected").val().substring($("#read_rsc_id :selected").val().indexOf("/")+1,
					$("#read_rsc_id :selected").val().indexOf("!"));
	//		alert(rscpth + " " + $("#read_obj_ist_id :selected").text()+ " " + $("#read_rsc_id :selected").text());
	    $.ajax({url: "http://localhost:8080/Restful_WebService/readclient/${devid}/"+rscpth, 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
	    		$("#read_result").html("<table></table>");
	            var newhd = $('<tr class="dy_created">').append(
	                    $('<th>').append("object id"),
	                    $('<th>').append("obj instance"),
	    	            $('<th>').append("rsc id"),
	                    $('<th>').append("rsc value")
	                );
	            $("#read_result table").append(newhd);
		    	$.each($.parseJSON(result), function(idx, obj) {
		    		//alert(obj.rsciddes);
		            var newtr = $('<tr class="dy_created">').append(
		                    $('<td>').append(
		                    		$('<input type="text" name='+obj.objid+' readonly>').val(obj.objiddes)	),
		                    $('<td>').append(
		    	               		$('<input type="text" name='+obj.objist+' readonly>').val(obj.objist)	),
		    	            $('<td>').append(
		    	                    $('<input type="text" name='+obj.rscid+' readonly>').val(obj.rsciddes)	),
		                    $('<td>').append(
		                    		$('<input type="text" class="dy_txt" name='+obj.objid+'.'+obj.objist+'.'+obj.rscid+' readonly>').val(obj.rscval)	)
		                );
		            $("#read_result table").append(newtr);
		    	});	
	      //  $("#read_result").html(result);
	    }});
	});
	
	
	$("#Discover").click(function(){
		
		var rscpth = $("#disc_obj_id :selected").val();
		
		if($("#disc_obj_ist_id :selected").text()!="Null")
			rscpth = rscpth +"*" + $("#disc_obj_ist_id").val();
		if($("#disc_obj_ist_id :selected").text()=="Null"&&$("#disc_rsc_id :selected").text()!="Null")
			rscpth = rscpth +"*" + "0";
		if($("#disc_rsc_id :selected").text()!="Null")	
			rscpth = rscpth +"*" + $("#disc_rsc_id :selected").val().substring($("#disc_rsc_id :selected").val().indexOf("/")+1,
					$("#disc_rsc_id :selected").val().indexOf("!"));
	//		alert(rscpth + " " + $("#read_obj_ist_id :selected").text()+ " " + $("#read_rsc_id :selected").text());
	    $.ajax({url: "http://localhost:8080/Restful_WebService/discoverclient/${devid}/"+rscpth, 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
			result = result.replace(/\</g, '&lt;');
			result = result.replace(/\>/g, '&gt;');
	        $("#disc_result").html(result);
	    }});
	});
	
	
	$("#Write").click(function(){
		
		var rscpth = $("#write_obj_id :selected").val();
		
		if($("#write_obj_ist_id :selected").text()!="Null")
			rscpth = rscpth +"*" + $("#write_obj_ist_id").val();
		if($("#write_obj_ist_id :selected").text()=="Null"&&$("#write_rsc_id :selected").text()!="Null")
			rscpth = rscpth +"*" + "0";
		if($("#write_rsc_id :selected").text()!="Null")	
			rscpth = rscpth +"*" + $("#write_rsc_id :selected").val().substring($("#write_rsc_id :selected").val().indexOf("/")+1,
					$("#write_rsc_id :selected").val().indexOf("!"));
		//	alert("http://localhost:8080/Restful_WebService/writeclient/${devid}/"+rscpth+"/"+$("#write_value").val());
		    $.ajax({url: "http://localhost:8080/Restful_WebService/writeclient/${devid}/"+rscpth+"/"+$("#write_value").val(), 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
	        $("#write_result").html(result);
	    }}); 
	});
	
	
	$("#Write_attribute").click(function(){
		
		var attpth = $("#write_att_obj_id :selected").val();

		if($("#write_att_rsc_id :selected").text()!="Null")	
			attpth = attpth +"*" + $("#write_att_rsc_id :selected").val().substring($("#write_att_rsc_id :selected").val().indexOf("/")+1,
					$("#write_att_rsc_id :selected").val().indexOf("!"));            
			//alert("http://localhost:8080/Restful_WebService/writeattclient/${devid}/"+attpth+"/"+$("#write_att_attribute_id :selected").text()+"/"+$("#write_att_value").val());
		    $.ajax({url: "http://localhost:8080/Restful_WebService/writeattclient/${devid}/"+attpth+"/"+$("#write_att_attribute_id :selected").val()+"/"+$("#write_att_value").val(), 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
	        $("#write_att_result").html(result);
	    }});  
	});
	
	
	$("#Execute").click(function(){
		
		var rscpth = $("#execute_obj_id :selected").val();

		if($("#execute_obj_ist_id :selected").text()!="Null")
			rscpth = rscpth +"*" + $("#execute_obj_ist_id").val();
		if($("#execute_obj_ist_id :selected").text()=="Null"&&$("#execute_rsc_id :selected").text()!="Null")
			rscpth = rscpth +"*" + "0";
		if($("#execute_rsc_id :selected").text()!="Null")	
			rscpth = rscpth +"*" + $("#execute_rsc_id :selected").val().substring($("#execute_rsc_id :selected").val().indexOf("/")+1,
					$("#execute_rsc_id :selected").val().indexOf("!"));            
	
	//	alert("http://localhost:8080/Restful_WebService/executeclient/${devid}/"+rscpth+"/"+$("#execute_command_id :selected").val());
 		    $.ajax({url: "http://localhost:8080/Restful_WebService/executeclient/${devid}/"+rscpth+"/"+$("#execute_command_id :selected").val(), 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
	        $("#execute_result").html(result);
	    }});  
	});
	
	
	$("#Create").click(function(){
		
		var rscpth = $("#create_obj_id :selected").val();

		if($("#create_rsc_id :selected").text()!="Null")	
			rscpth = rscpth +"*" + $("#create_rsc_id :selected").val().substring($("#create_rsc_id :selected").val().indexOf("/")+1,
					$("#create_rsc_id :selected").val().indexOf("!"));
			//alert("http://localhost:8080/Restful_WebService/createinstanceclient/${devid}/"+rscpth+"/"+$("#createinstance_resource_value").val());
		    $.ajax({url: "http://localhost:8080/Restful_WebService/createinstanceclient/${devid}/"+rscpth+"/"+$("#createinstance_resource_value").val(), 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
	        $("#create_result").html(result);
	    }});  
	});
	
	
	
	$("#Delete").click(function(){
		
		var rscpth = $("#delete_obj_id :selected").val();
		rscpth = rscpth +"*" + $("#delete_obj_ist_id").val();

		//	alert("http://localhost:8080/Restful_WebService/deleteinstanceclient/${devid}/"+rscpth);
 		    $.ajax({url: "http://localhost:8080/Restful_WebService/deleteinstanceclient/${devid}/"+rscpth, 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
	        $("#delete_result").html(result);
	    }});   
	});	
	
	
	$("#update_active").click(function(){
		    $.ajax({url: "http://localhost:8080/Restful_WebService/changeactivegp/${devid}/"+$("#active_gp :selected").val(), 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
	        alert("active group changed");
	    }}); 
	});
	
	
	$("#add_group").click(function(){
	    $.ajax({url: "http://localhost:8080/Restful_WebService/newgroup/${devid}/"+$("#new_group").val(), 
    	type: "GET",
    	DataType: "text",
    	error: function(xhr){
            alert("An error occured: " + xhr.status + " " + xhr.statusText);
        }, 
    	success: function(result){
        alert("New group added");
    }}); 
});
	
	$("#delete_group").click(function(){
	    $.ajax({url: "http://localhost:8080/Restful_WebService/deletegroup/${devid}/"+$("#multiple_list :selected").val(), 
    	type: "GET",
    	DataType: "text",
    	error: function(xhr){
            alert("An error occured: " + xhr.status + " " + xhr.statusText);
        }, 
    	success: function(result){
        alert("Group deleteded");
        document.location.reload(true);
    }}); 
});
	
	
	$("#multiple_list").change(function(){
		group_info_Ajax();
		if($("#multiple_list :selected").text().indexOf("***")>-1)
			{
				$("#delete_group").prop("disabled",false);
				$("#add_device").prop("disabled",false);
			}
		else
			{
				$("#delete_group").prop("disabled",true);
				$("#add_device").prop("disabled",true);
			}
	});	
	
	
	$("#add_device").click(function(){
	    $.ajax({url: "http://localhost:8080/Restful_WebService/adddevice/${devid}/"+$("#multiple_list :selected").val()+"/"+$("#new_device").val(), 
    	type: "GET",
    	DataType: "text",
    	error: function(xhr){
            alert("An error occured: " + xhr.status + " " + xhr.statusText);
        }, 
    	success: function(result){
        alert(result);
    }}); 
});
	
	
	//alert('${devid}');
	callAjax();
	getnotify();
	//alert("${group_info}");
	device_group();
	

	
	function callAjax(){

	    $.ajax({url: "http://localhost:8080/Restful_WebService/getobjrsc", 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){	 	       	
		    	$.each($.parseJSON(result), function(idx, obj) {
		     		if(obj.substring(1,2)=="!")
		     			$(".obj_id_sel").append($("<option></option>").attr("value",obj.substring(0,1)).text(obj));
		     		else
		     			$(".rsc_id_sel").append($("<option></option>").attr("value",obj).text(obj));
		    	});
		    	$(".obj_ist_id_sel").append($("<option></option>").attr("value",0).text(0));
		    	$(".obj_ist_id_sel").append($("<option></option>").attr("value",1).text(1));
		    	$(".obj_ist_id_sel").append($("<option></option>").attr("value",2).text(2));
		    	$(".obj_ist_id_sel").append($("<option></option>").attr("value",3).text(3));
		    	$(".obj_ist_id_sel").append($("<option></option>").attr("value",4).text(4));
	 	       

		    	 
	    }
	        });
	};
	
	

	function getnotify(){
		
		
	    $.ajax({url: "http://localhost:8080/Restful_WebService/getnotify", 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
	 	        /* window.location.reload(); */
		    	$.each($.parseJSON(result), function(idx, obj) {

			    	$.each($.parseJSON(result), function(idx, obj) {
			            var newtr = $('<tr class="dy_created">').append(
			                    $('<td>').append($('<input type="text" class = "notify_txt" readonly>').val(obj)	)
			                );
			            $("#notify_result_table").append(newtr);
			    	});	
		    	});
		    	 
	    },
	    complete: function() {
	        setTimeout(getnotify, 1500);
	      }
	        });
	};
	
	function group_info_Ajax(){
	    $.ajax({url: "http://localhost:8080/Restful_WebService/gpdvlst/"+$("#multiple_list :selected").val(), 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){
	 	       	
	 	        $("#group_device_list #dy_created").remove();
	 	        
	 	       var newtr="";
                if($("#multiple_list :selected").text().indexOf("***")>-1)		
                {
		    	$.each($.parseJSON(result), function(idx, obj) {
		    		
		    		if(obj!='${devid}')
		            {
		    			newtr = $('<tr id="dy_created">').append(
		                    $('<td>').append(
		                    		$('<input type="text" style="width:255px" name='+obj+' readonly>').val(obj)	),
		                    	$('<td>').append(
		    	    	            $('<button type="button" style="width:50px" id='+obj+' onclick="device_delete(this.id)">Delete</button>'))          
		                      
		            );  
		            }
		    		else
		    		{
		    			newtr = $('<tr id="dy_created">').append(
			                    $('<td>').append(
			                    		$('<input type="text" style="width:255px" name='+obj+' readonly>').val(obj)	)      
			            );  
		    		}
		    		 $("#group_device_list").append(newtr);
		    	});
	    		}
                else
                {
    		    	$.each($.parseJSON(result), function(idx, obj) {
    		          newtr = $('<tr id="dy_created">').append(
    		                    $('<td>').append(
    		                    		$('<input type="text" style="width:255px" name='+obj+' readonly>').val(obj)	)
    		            );
    		          $("#group_device_list").append(newtr);
    		    	});	
               	}
                
               
		    	 
	    }
	        });
	}; 
	
	
	function device_group(){
     	var act_dev = $.parseJSON('${group_info}').active;
     	$.each($.parseJSON('${group_info}').list, function(idx, obj){
     		
     		var gp_status;
     		gp_status = obj;
     		
     		$.each($.parseJSON('${gplst}'),function(idx2, obj2){
     		if(obj2 == obj)
     			gp_status = obj+" ***";
     		});
     		
     		$("#multiple_list").append($("<option></option>").attr("value",obj).text(gp_status));
     		if(act_dev == obj)
     			$("#active_gp").append($("<option></option>").attr("value",obj).attr("selected", true).text(obj));
     		else
     			$("#active_gp").append($("<option></option>").attr("value",obj).text(obj));
     		

     	});
     	
     	
     	$("#Request_list #dy_created").remove();
     	$.each($.parseJSON('${group_info}').gprst, function(idx, obj){
	    		
     		if(obj.status=="")
     			{
     		//	alert(obj.from+" "+obj.group);
	    			newtr = $('<tr id="dy_created">').append(
	                    $('<td>').append(
	    	               		$('<input type="text" style="width:80px" name='+obj.group+' readonly>').val(obj.group)),	
	                    $('<td>').append(
	    	    	            $('<button type="button" style="width:50px" name='+obj.from+' id='+obj.group+' onclick="accept_group(this.id,this.name)">Accept</button>')),
	    	    	   $('<td>').append(
	    	    	            $('<button type="button" style="width:50px" name='+obj.from+' id='+obj.group+' onclick="reject_group(this.id,this.name)">Reject</button>')),
	    	           $('<td>').append(
	    	                    $('<input type="text" style="width:255px" name='+obj.from+' readonly>').val(obj.from))
	            );  
	            
	    		 $("#Request_list").append(newtr);
	    		 
     			}
     	});
          

	}; 
	
	
	function refresh_device_group(){
		
	    $.ajax({url: "http://localhost:8080/Restful_WebService/refreshgroup/${devid}", 
	    	type: "GET",
	    	DataType: "text",
	    	error: function(xhr){
	            alert("An error occured: " + xhr.status + " " + xhr.statusText);
	        }, 
	    	success: function(result){

	    		$("#multiple_list #dy_created").remove();
	         	$.each($.parseJSON(result).list, function(idx, obj){
	         		
	         		var gp_status;
	         		gp_status = obj;
	         		
	         		$.each($.parseJSON('${gplst}'),function(idx2, obj2){
	         		if(obj2 == obj)
	         			gp_status = obj+" ***";
	         		});
	         		
	         		$("#multiple_list").append($("<option></option>").attr("value",obj).text(gp_status));
	         		if(act_dev == obj)
	         			$("#active_gp").append($("<option></option>").attr("value",obj).attr("selected", true).text(obj));
	         		else
	         			$("#active_gp").append($("<option></option>").attr("value",obj).text(obj));
	         		

	         	});
	    }}); 

          

	}; 

	
});

function device_delete(id){
	
    $.ajax({url: "http://localhost:8080/Restful_WebService/deletedevice/${devid}/"+$("#multiple_list :selected").val()+"/"+id, 
    	type: "GET",
    	DataType: "text",
    	error: function(xhr){
            alert("An error occured: " + xhr.status + " " + xhr.statusText);
        }, 
    	success: function(result){
        alert("Device deleteded");
        document.location.reload(true);
    }}); 
	
	
}; 

function accept_group(groupname, from){
	
    $.ajax({url: "http://localhost:8080/Restful_WebService/acceptgroup/${devid}/"+groupname+"/"+from, 
    	type: "GET",
    	DataType: "text",
    	error: function(xhr){
            alert("An error occured: " + xhr.status + " " + xhr.statusText);
        }, 
    	success: function(result){
        alert("Group joined");
        document.location.reload(true);
    }}); 
	
	
}; 

function reject_group(groupname, from){
	
    $.ajax({url: "http://localhost:8080/Restful_WebService/rejectgroup/${devid}/"+groupname+"/"+from, 
    	type: "GET",
    	DataType: "text",
    	error: function(xhr){
            alert("An error occured: " + xhr.status + " " + xhr.statusText);
        }, 
    	success: function(result){
        alert("Group rejected");
        document.location.reload(true);
    }}); 
	
	
}; 

</script>

</head>
<body>

<h3>${devid}</h3>

<table id = "testid">
<tr>
<td>
<div id = "div_layout">
<table id="manage_layout">
<tr><td><table><tr>
<td><button type="button" id="Read" onclick="">Read</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "read_obj_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "read_obj_ist_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "read_rsc_id">
  <option value="">Null</option>
</select>
</td>
</tr></table></td></tr>
<tr><td><div id = "read_result" class="rst_div"></div></td></tr>

<tr><td><table><tr>
<td><button type="button" id="Discover" onclick="">Discover</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "disc_obj_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "disc_obj_ist_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "disc_rsc_id">
  <option value="">Null</option>
</select>
</td>
</tr></table></td></tr>
<tr><td><div  class="rst_div" id = "disc_result"></div></td></tr>

<tr><td><table><tr>
<td><button type="button" id="Write" onclick="">Write</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "write_obj_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "write_obj_ist_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "write_rsc_id">
  <option value="">Null</option>
</select>
</td>
</tr>
<tr>
<td></td>
<td><a>Value</a>
<input type="text" id = "write_value">
</td>
</tr>
</table></td></tr>
<tr><td><div class="rst_div" id = "write_result"></div></td></tr>

<tr><td><table><tr>
<td><button type="button" id="Write_attribute" onclick="">Write Attribute</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "write_att_obj_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "write_att_obj_ist_id" disabled>
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "write_att_rsc_id">
  <option value="">Null</option>
</select>
</td>
</tr>
<tr>
<td></td>
<td><a>Attribute</a>
<select class = "attribute_sel" id = "write_att_attribute_id">
  <option value="">Null</option>
  <option value="pmin">Minimum Period</option>
  <option value="pmax">Maximum Period</option>
  <option value="gt">Greater Than</option>
  <option value="lt">Less Than</option>
  <option value="st">Step</option>
  <option value="cancel">Cancel</option>
</select></td>
<td><a>Value</a>
<input type="text" id = "write_att_value">
</td>
</tr>
</table></td></tr>
<tr><td><div class="rst_div" id = "write_att_result"></div></td></tr>

<tr><td><table><tr>
<td><button type="button" id="Execute" onclick="">Execute</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "execute_obj_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "execute_obj_ist_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "execute_rsc_id">
  <option value="">Null</option>
</select>
</td>
</tr>
<tr>
<td></td>
<td><a>command</a>
<select class = "command_sel" id = "execute_command_id">
  <option value="Null">Null</option>
  <option value="light_on">Indicator Light On</option>
  <option value="light_off">Indicator Light Off</option>
</select></td>
</tr>
</table></td></tr>
<tr><td><div class="rst_div" id = "execute_result"></div></td></tr>

<tr><td><table><tr>
<td><button type="button" id="Create" onclick="">Create</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "create_obj_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "create_obj_ist_id" disabled>
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "create_rsc_id">
  <option value="">Null</option>
</select>
</td>
</tr>
<tr>
<td></td>
<td><a>Value</a>
<input type="text" id = "createinstance_resource_value">
</td>
</tr>
</table></td></tr>
<tr><td><div class="rst_div" id = "create_result"></div></td></tr>

<tr><td><table><tr>
<td><button type="button" id="Delete" onclick="">Delete</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "delete_obj_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "delete_obj_ist_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "delete_rsc_id" disabled>
  <option value="">Null</option>
</select>
</td>
</tr></table></td></tr>
<tr><td><div class="rst_div" id = "delete_result"></div></td></tr>

<!-- 
<tr><td><table><tr>
<td><button type="button" id="Observe" onclick="">Observe</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "observe_obj_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "observe_obj_ist_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "observe_rsc_id">
  <option value="">Null</option>
</select>
</td>
</tr></table></td></tr>
<tr><td><div class="rst_div"></div></td></tr>
 -->
<tr><td><table><tr>
<td><button type="button" id="Notify" onclick="" disabled>Notify</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "notify_obj_id" disabled>
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "notify_obj_ist_id" disabled>
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "notify_rsc_id" disabled>
  <option value="">Null</option>
</select>
</td>
</tr></table></td></tr>
<tr><td><div class="rst_div" id = "notify_result"><table id = "notify_result_table"></table></div></td></tr>
<!--  
<tr><td><table><tr>
<td><button type="button" id="Cancel_observation" onclick="">Cancel Observation</button></td>
<td><a>Object id</a>
<select class = "obj_id_sel" id = "cancel_obv_obj_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Object Instance id</a>
<select  class = "obj_ist_id_sel" id = "cancel_obv_obj_ist_id">
  <option value="">Null</option>
</select>
</td>
<td><a>Resource id</a>
<select class = "rsc_id_sel" id = "cancel_obv_rsc_id">
  <option value="">Null</option>
</select>
</td>
</tr></table></td></tr>
<tr><td><div class="rst_div"></div></td></tr>
-->
</table>
</div>
</td>
<td>
<div id = "group_layout"><table id = "group_table">
<tr style="height:350px;"><td><h3>Your Groups</h3>
<div><select id="multiple_list" size="5" >
</select>
<button type="button" id="delete_group" onclick="">Delete Group</button>
</div>
<p>Active Group--------------</p>
<div><select style="width:150px" id="active_gp">
<option value="disabled"></option>
</select><button type="button" id="update_active" onclick="">Change</button></div>
<p>-------------------------------</p>
<div><input type="text" id="new_device"><button type="button" id="add_device" onclick="">Add Device</button></div>
<p>-------------------------------</p>
<div><input type="text" id="new_group"><button type="button" id="add_group" onclick="">New Group</button></div>
</td>
<td style="width:350px"><h3>Groups Device List</h3><div id="device_list_div"><table id="group_device_list">

</table></div><p>--------Request---------</p><div id="request_list_div"><table id="Request_list">

</table></div></td></tr>
<tr><td></td><td></td></tr>
<tr><td></td><td></td></tr>

</table></div>
</td>
</tr>
</table>

</body>
</html>