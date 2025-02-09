package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.customer.dto.response.AddressBookResDTO;
import com.jzo2o.api.foundations.dto.response.ServeItemResDTO;
import com.jzo2o.api.publics.MapApi;
import com.jzo2o.api.publics.dto.response.LocationResDTO;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.common.utils.NumberUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.common.utils.StringUtils;
import com.jzo2o.customer.mapper.AddressBookMapper;
import com.jzo2o.customer.model.domain.AddressBook;
import com.jzo2o.customer.model.dto.request.AddressBookPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.jzo2o.customer.service.IAddressBookService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageHelperUtils;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 地址薄 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-07-06
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {


    @Resource
    private MapApi mapApi;


    @Override
    public List<AddressBookResDTO> getByUserIdAndCity(Long userId, String city) {

        List<AddressBook> addressBooks = lambdaQuery()
                .eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getCity, city)
                .list();
        if(CollUtils.isEmpty(addressBooks)) {
            return new ArrayList<>();
        }
        return BeanUtils.copyToList(addressBooks, AddressBookResDTO.class);
    }

    /**
     * 新增地址
     *
     * @param addressBookUpsertReqDTO 地址信息
     */
    @Override
    public void addAddressBook(AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        //0.设置经纬度
        LocationResDTO locationByAddress = mapApi.getLocationByAddress(addressBookUpsertReqDTO.getAddress());
        String location=locationByAddress.getLocation();
        Double lon= Double.valueOf(location.split(",")[0]);
        Double lat= Double.valueOf(location.split(",")[1]);
        AddressBook addressBook= BeanUtil.toBean(addressBookUpsertReqDTO, AddressBook.class);
        addressBook.setLon(lon);
        addressBook.setLat(lat);

        //1.获取当前用户id
        Long userId = UserContext.currentUserId();
        addressBook.setUserId(userId);
        //2.默认地址处理
        if(addressBook.getIsDefault().equals(1)) {
            //2.1.查询当前用户的默认地址
            AddressBook defaultAddress = lambdaQuery()
                .eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getIsDefault, "1")
                .one();
            //2.2.如果有默认地址，将其改为非默认
            if(defaultAddress != null) {
                defaultAddress.setIsDefault(0);
                updateById(defaultAddress);
            }
        }
        //3.新增地址
        save(addressBook);
    }

    /**
     * 地址溥分页查询
     *
     * @param addressBookPageQueryReqDTO
     */
    @Override
    public PageResult<AddressBook> page(AddressBookPageQueryReqDTO addressBookPageQueryReqDTO) {
        //1.获取当前用户id
        Long userId = UserContext.currentUserId();
        Page<AddressBook> addressBookPage = PageUtils.parsePageQuery(addressBookPageQueryReqDTO, AddressBook.class);
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<AddressBook>().eq("user_id", userId).eq("is_deleted",0);
        Page<AddressBook> bookPage = baseMapper.selectPage(addressBookPage, queryWrapper);
        return PageUtils.toPage(bookPage,AddressBook.class);
    }

    /**
     * 修改地址
     *
     * @param id
     * @param addressBookUpsertReqDTO
     */
    @Override
    @Transactional
    public AddressBook updateAddress(Long id, AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        LocationResDTO locationByAddress = mapApi.getLocationByAddress(addressBookUpsertReqDTO.getAddress());
        String location = locationByAddress.getLocation();
        Double lon = Double.valueOf(location.split(",")[0]);
        Double lat = Double.valueOf(location.split(",")[1]);
        AddressBook addressBook = BeanUtil.toBean(addressBookUpsertReqDTO, AddressBook.class);
        addressBook.setLon(lon);
        addressBook.setLat(lat);
        if(addressBook.getIsDefault().equals(1)) {
            AddressBook defaultAddress = lambdaQuery()
                    .eq(AddressBook::getUserId, UserContext.currentUserId())
                    .eq(AddressBook::getIsDefault, "1")
                    .ne(AddressBook::getId,id)
                    .one();
            if (defaultAddress != null) {
                defaultAddress.setIsDefault(0);
                updateById(defaultAddress);
            }
        }
        //3.更新地址
        addressBook.setId(id);
        updateById(addressBook);
        return addressBook;
    }

    /**
     * 设置默认地址
     *
     * @param id
     * @param flag
     * @return
     */
    @Override
    @Transactional
    public AddressBook setDefault(Long id, Integer flag) {
        AddressBook defaultAddressBook = lambdaQuery().eq(AddressBook::getUserId, UserContext.currentUserId())
            .eq(AddressBook::getIsDefault, "1")
            .ne(AddressBook::getId, id)
            .one();
        if (defaultAddressBook != null) {
            defaultAddressBook.setIsDefault(0);
            updateById(defaultAddressBook);
        }
        AddressBook addressBook = getById(id);
        addressBook.setIsDefault(flag);
        updateById(addressBook);
        return addressBook;
    }

}
