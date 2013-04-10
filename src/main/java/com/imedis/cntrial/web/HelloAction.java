package com.imedis.cntrial.web;

import com.imedis.cntrial.model.Account;
import com.imedis.cntrial.service.HelloService;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-4-9
 * Time: 上午11:49
 * To change this template use File | Settings | File Templates.
 */
public class HelloAction extends ActionSupport {
    private HelloService service;
    private Account account;
    public String execute() throws Exception {
        account = new Account();
        account.setUsername(service.hello());
        return SUCCESS;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setService(HelloService s) {
        service = s;
    }
}
