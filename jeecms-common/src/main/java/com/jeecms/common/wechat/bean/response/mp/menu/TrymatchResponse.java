package com.jeecms.common.wechat.bean.response.mp.menu;

import com.jeecms.common.wechat.bean.request.mp.menu.common.Menu;
import com.jeecms.common.wechat.bean.response.mp.BaseResponse;

/**
 * 
 * TODO
 * @author: tom
 * @date:   2019年3月8日 下午4:43:12
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class TrymatchResponse extends BaseResponse{
	/**
	 * 菜单接口
	 */
	private Menu menu=new Menu();

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
}
