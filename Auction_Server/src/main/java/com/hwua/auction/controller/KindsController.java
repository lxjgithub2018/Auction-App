package com.hwua.auction.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.hwua.auction.po.Kinds;
import com.hwua.auction.service.KindsService;
import com.hwua.auction.util.JsonUtils;
import com.hwua.auction.util.Page;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 *  商品类别管理控制层
 *
 */
public class KindsController extends ActionSupport implements ModelDriven<Object> {

	// log4j日志对象
	private Logger log = Logger.getLogger(this.getClass());
	
	//用于接收表单数据
	private Kinds kinds = new Kinds();

	private Integer page;//当前页
	private Integer rows;//一页显示的记录数

	@Resource //告诉spring，当扫描到这里时，注入kindsService对象进来，初始此属性
	private KindsService kindsService;//接收业务逻辑层对象

	/**
	 *  商品类别添加页面
	 * 
	 * @return
	 */
	@Action(value = "/kinds_add", results = {
			@Result(name = "input", location = "/WEB-INF/content/KindsAdd.jsp") })
	public String add(){
		log.info("进入 商品类别添加页面");
		return "input";
	}
	
	/**
	 * 保存添加
	 * 
	 * @return
	 */
	@Action("/kinds_addSave")
	public String addSave() {

		Map jsonDatas = new HashMap();// 存放json数据的集合
		jsonDatas.put("status", 0);// 默认状态为0，表示操作失败
		try {
			boolean flag = kindsService.add(kinds);// 然后把kinds添加进去
			if (flag) {
				jsonDatas.put("status", 1);// 设置状态为1，表示操作成功
			}

		} catch (Exception e) {
			log.error("添加失败", e);
		}
		// 最终给JsonUtils转换为json数据输出给浏览器
		ServletActionContext.getRequest().setAttribute("jsonDatas", JsonUtils.objectToJson(jsonDatas));
		return "json";

	}

	/**
	 *  商品类别修改页面
	 * 
	 * @return
	 */
	@Action(value = "/kinds_edit", results = {
			@Result(name = "input", location = "/WEB-INF/content/KindsEdit.jsp") })
	public String edit(){
		log.info("进入 商品类别修改页面");
		return "input";
	}
	
	/**
	 * 初始化修改
	 * 
	 * @return
	 */
	@Action("/kinds_update")
	public String update() {
		Integer id = kinds.getKindId();
		log.info("开始初始化编号为" + id + "的 商品类别数据供前端修改");// 把id放到日志
		Kinds kinds = null;
		try {
			// 通过id去获取 商品类别信息
			kinds = kindsService.get(id);// 通过id拿到种类对象赋给kinds变量
		} catch (Exception e) {
			log.error("初始化修改失败", e);
		}
		// 最终给JsonUtils转换为json数据输出给浏览器
		ServletActionContext.getRequest().setAttribute("jsonDatas", JsonUtils.objectToJson(kinds));
		return "json";

	}

	/**
	 * 保存修改
	 * 
	 * @return
	 */
	@Action("/kinds_updateSave")
	public String updateSave() {

		log.info("接收到页面修改的数据：" + kinds);// 把kinds放到日志
		Map jsonDatas = new HashMap();// 存放json数据的集合
		jsonDatas.put("status", 0);// 默认状态为0，表示操作失败
		try {

			Kinds oldKinds = kindsService.get(kinds.getKindId());// 通过id找到对应的这一条 商品类别信息
			// 找到之后，把名字放进去给它
			oldKinds.setKindName(kinds.getKindName());
			oldKinds.setKindDesc(kinds.getKindDesc());
			
			// 调用修改的方法，修改原先的信息
			boolean flag = kindsService.update(oldKinds);

			if (flag) {
				jsonDatas.put("status", 1);// 设置状态为1，表示操作成功
			}

		} catch (Exception e) {
			log.error("修改失败", e);
		}
		// 最终给JsonUtils转换为json数据输出给浏览器
		ServletActionContext.getRequest().setAttribute("jsonDatas", JsonUtils.objectToJson(jsonDatas));
		return "json";
	}

	/**
	 * 删除 商品类别
	 * 
	 * @return
	 
	 */
	@Action("/kinds_delete")
	public String delete() {
		Integer id = kinds.getKindId();
		log.info("开始删除编号为" + id + "的 商品类别");// 把id放到日志
		Map jsonDatas = new HashMap();// 存放json数据的集合
		jsonDatas.put("status", 0);// 默认状态为0，表示操作失败
		try {
			// 通过id去删除数据
			boolean flag = kindsService.delete(id);
			if (flag) {
				jsonDatas.put("status", 1);// 设置状态为1，表示操作成功
			}
		} catch (Exception e) {
			log.error("删除失败", e);
		}
		// 最终给JsonUtils转换为json数据输出给浏览器
		ServletActionContext.getRequest().setAttribute("jsonDatas", JsonUtils.objectToJson(jsonDatas));
		return "json";
	}

	/**
	 *  商品类别查询页面
	 * 
	 * @return
	 */
	@Action(value = "/kinds_search", results = {
			@Result(name = "input", location = "/WEB-INF/content/KindsSearch.jsp") })
	public String search(){
		log.info("进入 商品类别查询页面");
		return "input";
	}
	
	/**
	 *  商品类别管理页面
	 * 
	 * @return
	 */
	@Action(value = "/kinds_select", results = {
			@Result(name = "input", location = "/WEB-INF/content/KindsList.jsp") })
	public String select() {
		log.info("进入 商品类别管理页面");
		return "input";
	}

	/**
	 *  商品类别管理表格数据
	 * @return
	 
	 */
	@Action("/kinds_selectDatas")
	public String selectDatas() {

		log.info("开始获取 商品类别管理表格数据");
		Map jsonDatas = new HashMap();// 待返回json集合数据;
		try {
			// 分页处理
			Page<Kinds> pageObj = kindsService.getList(kinds, page, rows);
			// 取记录总条数
			jsonDatas.put("total", pageObj.getTotal());// 总数
			jsonDatas.put("rows", pageObj.getResultlist());// 前端需要的行数据

		} catch (Exception e) {
			log.error("获取 商品类别管理表格数据失败", e);
		}
		// 最终给JsonUtils转换为json数据输出给浏览器
		ServletActionContext.getRequest().setAttribute("jsonDatas", JsonUtils.objectToJson(jsonDatas));
		return "json";

	}

	@Override
	public Object getModel() {
		return kinds;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

}