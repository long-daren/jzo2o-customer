package com.jzo2o.customer.controller.consumer;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.api.foundations.dto.response.ServeItemResDTO;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.AddressBook;
import com.jzo2o.customer.model.dto.request.AddressBookPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.jzo2o.customer.service.IAddressBookService;
import com.jzo2o.mysql.utils.PageUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("AddressBookController")
@RequestMapping("/consumer/address-book")
@Api(tags = "用户端 - 地址薄相关接口")
public class AddressBookController {

    @Resource
    private IAddressBookService addressBookService;

    @PostMapping()
    @ApiOperation("新增用户地址谱")
    public void addAddressBook(@RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        addressBookService.addAddressBook(addressBookUpsertReqDTO);
    }


    @GetMapping("/page")
    @ApiOperation("地址薄分页查询")
    public PageResult<AddressBook> page(AddressBookPageQueryReqDTO addressBookPageQueryReqDTO) {
        PageResult<AddressBook> page = addressBookService.page(addressBookPageQueryReqDTO);
        return page;
    }

    @GetMapping("/{id}")
    @ApiOperation("按id查询地址薄")
    public AddressBook getById(@PathVariable("id") Long id) {
        return addressBookService.getById(id);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改地址薄")
    public AddressBook update(@PathVariable("id") Long id,@RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        return addressBookService.updateAddress(id,addressBookUpsertReqDTO);
    }

    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public AddressBook setDefault(@RequestParam("id") Long id, @RequestParam("flag") Integer flag){
        return addressBookService.setDefault(id, flag);
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除地址薄")
    public void deleteBatch(@RequestBody List<Long> ids) {
        addressBookService.removeByIds(ids);
    }

}
