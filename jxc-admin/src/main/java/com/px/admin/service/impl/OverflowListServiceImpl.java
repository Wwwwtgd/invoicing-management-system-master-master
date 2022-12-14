package com.px.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.px.admin.pojo.Goods;
import com.px.admin.pojo.OverflowList;
import com.px.admin.mapper.OverflowListMapper;
import com.px.admin.pojo.OverflowListGoods;
import com.px.admin.query.OverFlowListQuery;
import com.px.admin.service.GoodsService;
import com.px.admin.service.OverflowListGoodsService;
import com.px.admin.service.OverflowListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.px.admin.utils.AssertUtil;
import com.px.admin.utils.DateUtil;
import com.px.admin.utils.PageResultUtil;
import com.px.admin.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class OverflowListServiceImpl extends ServiceImpl<OverflowListMapper, OverflowList> implements OverflowListService {

    @Resource
    private GoodsService goodsService;

    @Resource
    private OverflowListGoodsService overflowListGoodsService;

    @Override
    public String getOverflowNumber() {
        try {
            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append("BY");
            stringBuffer.append(DateUtil.getCurrentDateStr());
            String saleNumber = this.baseMapper.getOverflowNumber();
            if(null !=saleNumber){
                stringBuffer.append(StringUtil.formatCode(saleNumber));
            }else{
                stringBuffer.append("0001");
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void saveOverflowList(OverflowList overflowList, List<OverflowListGoods> plgList) {
        AssertUtil.isTrue(overflowList.getOverflowDate()==null,"???????????????");
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        AssertUtil.isTrue(0!= formatter.format(overflowList.getOverflowDate()).compareTo(formatter.format(date)),"?????????????????????");
        AssertUtil.isTrue(!(this.save(overflowList)),"??????????????????!");
        OverflowList temp = this.getOne(new QueryWrapper<OverflowList>().eq("overflow_number",overflowList.getOverflowNumber()));
        AssertUtil.isTrue(plgList==null,"???????????????");
        plgList.forEach(plg->{
            AssertUtil.isTrue(plg.getNum()==null,"??????????????????");
            plg.setOverflowListId(temp.getId());
            Goods goods = goodsService.getById(plg.getGoodsId());
            goods.setInventoryQuantity(goods.getInventoryQuantity()+plg.getNum());
            goods.setState(2);
            AssertUtil.isTrue(!(goodsService.updateById(goods)),"??????????????????!");
            AssertUtil.isTrue(!(overflowListGoodsService.save(plg)),"??????????????????!");
        });
    }

    @Override
    public Map<String, Object> overFlowList(OverFlowListQuery overFlowListQuery) {
        IPage<OverflowList> page = new Page<OverflowList>(overFlowListQuery.getPage(),overFlowListQuery.getLimit());
        page =  this.baseMapper.overFlowList(page,overFlowListQuery);
        return PageResultUtil.setResult(page.getTotal(),page.getRecords());
    }

    @Override
    public void deleteoverflowList(Integer id) {
        /**
         * 1.???????????????????????????
         * 2.?????????????????????
         */
        AssertUtil.isTrue(!(overflowListGoodsService.remove(new QueryWrapper<OverflowListGoods>().eq("overflow_list_id",id))),
                "??????????????????!");
        AssertUtil.isTrue(!(this.removeById(id)),"??????????????????!");
    }
}
