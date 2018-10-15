package com.hotent.platform.controller.form;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hotent.core.annotion.Action;
import com.hotent.core.util.UniqueIdUtil;
import com.hotent.core.web.ResultMessage;
import com.hotent.core.web.controller.BaseFormController;
import com.hotent.core.web.util.RequestUtil;

import com.hotent.platform.model.form.BpmFormTemplate;
import com.hotent.platform.service.form.BpmFormTemplateService;

/**
 * 对象功能:表单模板 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:xwy
 * 创建时间:2012-01-09 16:35:25
 */
@Controller
@RequestMapping("/platform/form/bpmFormTemplate/")
public class BpmFormTemplateFormController extends BaseFormController
{
	@Resource
	private BpmFormTemplateService bpmFormTemplateService;
	
	/**
	 * 添加或更新表单模板。
	 * @param request
	 * @param response
	 * @param bpmFormTemplate 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新表单模板")
	public void save(HttpServletRequest request, HttpServletResponse response, BpmFormTemplate bpmFormTemplate,BindingResult bindResult) throws Exception
	{
		
		ResultMessage resultMessage=validForm("bpmFormTemplate", bpmFormTemplate, bindResult, request);
		//add your custom validation rule here such as below code:
		//bindResult.rejectValue("name","errors.exist.student",new Object[]{"jason"},"重复姓名");
		if(resultMessage.getResult()==ResultMessage.Fail)
		{
			writeResultMessage(response.getWriter(),resultMessage);
			return;
		}
		String resultMsg=null;
		if(bpmFormTemplate.getTemplateId()==null){
			bpmFormTemplate.setTemplateId(UniqueIdUtil.genId());
			bpmFormTemplate.setCanEdit(1);
			String alias=bpmFormTemplate.getAlias();
			boolean isExist=bpmFormTemplateService.isExistAlias(alias);
			if(isExist){
				resultMsg=getText("该别名已经存在！");
				writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Fail);
				return;
			}else{
			    bpmFormTemplateService.add(bpmFormTemplate);
			    resultMsg=getText("record.added","表单模板");
			}
		}else{
			bpmFormTemplateService.update(bpmFormTemplate);
			resultMsg=getText("record.updated","表单模板");
		}
		writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
	}
	
	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param templateId
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected BpmFormTemplate getFormObject(@RequestParam("templateId") Long templateId,Model model) throws Exception {
		logger.debug("enter BpmFormTemplate getFormObject here....");
		BpmFormTemplate bpmFormTemplate=null;
		if(templateId!=null){
			bpmFormTemplate=bpmFormTemplateService.getById(templateId);
		}else{
			bpmFormTemplate= new BpmFormTemplate();
		}
		return bpmFormTemplate;
    }

}
