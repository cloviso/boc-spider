/**
 * Project Name:ctrm-bat
 * File Name:GetPurchaseSpotJob.java
 * Package Name:com.maikegroup.delight.ctrm.bat.transTask
 * Copyright (c) 2018, ****.
 *
 */
package com.maikegroup.delight.data.consumer;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.maikegroup.delight.data.repository.data.bocExchange.dao.BocExchangeMapper;
import com.maikegroup.delight.data.repository.data.bocExchange.model.BocExchange;
/**
 * ClassName: GetBocExchange <br/>
 * Function: TODO 获取中行美元汇率并持久化到数据库. <br/>
 * 
 * @author juan.yang
 */
@Component
public class GetBocExchange {
	@Autowired
	private BocExchangeMapper bocExchangeMapper;
	
	Logger logger = Logger.getLogger(GetBocExchange.class);

	public static final String url = "http://www.boc.cn/sourcedb/whpj/";
	public static final String companyName = "中国银行";
	public static final String moneyType = "美元";

	@Scheduled(cron = "0 0/5 * * * ? ") // 测试每五分钟执行一次
	public void task() throws Exception {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();

		} catch (IOException e1) {
			logger.warn("中国银行现汇网请求超时！！");
			return;
		}
		String title = doc.title();
		if (StringUtils.isBlank(title)) {
			logger.warn("中国银行现汇网请求超时！！");
			return;
		}
		logger.info("title:" + title);

		// 初始化中行外汇美元对应汇率
		String cashBuy = "0";
		String moneyBuy = "0";
		String cashSale = "0";
		String moneySale = "0";
		String midDiscount = "0";
		String launchDt = new Date().toString();
		// 主table上的DIV
		Element upTableDiv = doc.getElementById("DefaultMain");
		// 主DIV
		Element mainDiv = upTableDiv.parent();
		Element allElements = mainDiv.child(1);
		Elements trEles = allElements.getElementsByTag("tr");
		for (Element td1 : trEles) {
			Elements tdEles = td1.getAllElements();
			if (moneyType.equals(tdEles.get(1).text())) {
				cashBuy = tdEles.get(2).text();
				moneyBuy = tdEles.get(3).text();
				cashSale = tdEles.get(4).text();
				moneySale = tdEles.get(5).text();
				midDiscount = tdEles.get(6).text();
				launchDt = tdEles.get(7).text() + " " + tdEles.get(8).text();

				System.out.println("美元的现汇买入价为：" + cashBuy + ",现钞买入价：" + moneyBuy + ",现汇卖出价：" + cashSale + ",现钞卖出价："
						+ moneySale + ",中行折算价：" + midDiscount + ",发布时间：" + launchDt);
			}

		}

		// 每5分钟更新汇率数据 到 tb_boc_exchange 表中
		BocExchange bocExchange = new BocExchange();
		bocExchange.setMoneyType(moneyType);
		BocExchange model = bocExchangeMapper.selectByPrimaryKey(moneyType);

		if (null == model) {
			//bocExchange.setMoneyType(moneyType);
			bocExchange.setCashBuy(cashBuy);
			bocExchange.setMoneyBuy(moneyBuy);
			bocExchange.setCashSale(cashSale);
			bocExchange.setMoneySale(moneySale);
			bocExchange.setMidDiscount(midDiscount);
			bocExchange.setLaunchDt(launchDt);
			bocExchangeMapper.insertSelective(bocExchange);

		} else {
			bocExchange.setCashBuy(cashBuy);
			bocExchange.setMoneyBuy(moneyBuy);
			bocExchange.setCashSale(cashSale);
			bocExchange.setMoneySale(moneySale);
			bocExchange.setMidDiscount(midDiscount);
			bocExchange.setLaunchDt(launchDt);
			bocExchangeMapper.updateByPrimaryKeySelective(bocExchange);
		}

	}
}
