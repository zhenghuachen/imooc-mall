package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.filter.UserFilter;
import com.imooc.mall.model.dao.CartMapper;
import com.imooc.mall.model.dao.OrderItemMapper;
import com.imooc.mall.model.dao.OrderMapper;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Order;
import com.imooc.mall.model.pojo.OrderItem;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.model.vo.OrderItemVO;
import com.imooc.mall.model.vo.OrderVO;
import com.imooc.mall.service.CartService;
import com.imooc.mall.service.OrderService;
import com.imooc.mall.service.UserService;
import com.imooc.mall.util.OrderCodeFactory;
import com.imooc.mall.util.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单Service实现类
 */

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    CartService cartService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Value("${file.upload.ip}")
    String ip;

    @Autowired
    UserService userService;

    /**
     * @Transactional 注解可以应用在方法或类级别上，用于声明一个方法或类需要被事务管理。
     * 具体来说，@Transactional(rollbackFor = Exception.class) 的含义如下：
     * @Transactional：标记该方法或类需要进行事务管理。
     * rollbackFor = Exception.class：指定在哪些异常发生时需要回滚事务。Exception.class表示回滚
     * 所有的异常。
     * 使用 @Transactional 注解可以达到以下效果：
     * 当被注解的方法或类执行期间发生了受检异常或运行时异常时，Spring会自动回滚事务，将之前的数据库操作撤销。
     * 如果没有指定rollbackFor属性，那么默认情况下只有运行时异常会触发事务回滚。
     * 需要注意的是，@Transactional注解需要与Spring的事务管理器配合使用，以确保事务的正确执行和回滚。
     * 通常情况下，你需要在 Spring 配置文件中配置适当的事务管理器。
     * @param createOrderReq
     * @return
     */

    // 数据库事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public  String create(CreateOrderReq createOrderReq) {
        // 拿到用户ID
        Integer userId = UserFilter.currentUser.getId();
        // 从购物车查找到已经勾选的商品
        List<CartVO> cartVOList = cartService.list(userId);  //通过用户ID获取购物车列表
        ArrayList<CartVO> cartVOListTemp = new ArrayList<>();
        for (int i = 0; i < cartVOList.size() ; i++) {
            CartVO cartVO = cartVOList.get(i);
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;  //购物车列表仅仅包含选中的商品
        // 如果购物车已勾选的为空，报错
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CART_EMPTY);
        }

        // 判断商品是否存在、上下架状态、库存
        validSaleStatusAndStock(cartVOList);
        // 把购物车对象转为订单item对象
        List<OrderItem> orderItemList =  cartVOListToOrderItemList(cartVOList);
        // 扣库存
        for (int i = 0; i < orderItemList.size(); i++) {  // itil自动生成for循环
            OrderItem orderItem =  orderItemList.get(i);
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        // 把购物车中已勾选的商品删除
        cleanCart(cartVOList);
        // 生成订单
        Order order = new Order();
        // 生成订单号，有独立规则
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());//订单刚生成
        order.setPostage(0);
        order.setPaymentType(1);
        // 插入到order表
        orderMapper.insertSelective(order);
        // 循环保存每个商品到order_item表
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            orderItem.setOrderNo(order.getOrderNo());  // OrderItem增加orderNo
            orderItemMapper.insertSelective(orderItem);
        }
        // 把结果返回
        return orderNo;
    }

    // 获取订单总价
    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            // 根据ID删除购物车中已勾选商品
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            // 记录商品快照
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            // 判断商品是否存在，商品是否上架
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
            }
            // 判断商品库存
            if (cartVO.getQuantity() > product.getStock()) {  // 购买数量大于库存数量
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    @Override
    public OrderVO detail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 订单不存在，则报错
        if(order == null){
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        // 订单存在，需要判断所属
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        OrderVO orderVO = getOrderVO(order);
        return orderVO;
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        //  获取订单对应的orderItemVOList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();  //orderItemVOList相对orderItemList无新增字段
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        // 赋值OrdeItemVOList
        orderVO.setOrderItemVOList(orderItemVOList);
        // 数字类型-》枚举-》枚举的value
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return  orderVO;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
        Integer userId = UserFilter.currentUser.getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    @Override
    public  void cancel(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 查不到订单，报错
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        // 验证用户身份
        // 订单存在，判断所属
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        // 未付款，可取消订单
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());  // 修改订单状态
            order.setEndTime(new Date());  // 设置完结时间
            orderMapper.updateByPrimaryKeySelective(order);
        } else{
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    // 生成二维码，返回二维码图片地址
    @Override
    public String qrcode(String orderNo) {
        /**
         * 首先，通过RequestContextHolder.getRequestAttributes()获取当前请求的属性。
         * 然后，将请求对象转换为ServletRequestAttributes对象，以便获取请求相关信息。
         * 通过ServletRequestAttributes.getRequest()获取HttpServletRequest对象，从中获取本地端口号和IP地址。
         * 构建一个包含IP地址和本地端口号的字符串，命名为address。
         * 根据传入的 orderNo 参数拼接出二维码的链接地址 payUrl，格式为 "http://address/pay?orderNo=xx"。
         * 调用QRCodeGenerator.generatorQRCodeImage()方法生成二维码图片，并保存到指定路径下的文件中。
         * 将生成的二维码图片的地址拼接为完整的URL地址，并命名为pngAddress。
         * 返回 pngAddress，即生成的二维码图片的地址。
         */
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String address = ip + ":" + request.getLocalPort();
//        try {
//            // 获取本机电脑的局域网IP，复杂网络环境下此方法获取的IP并不准确,调试使用，部署前删除
//            ip = InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
        // 生成二维码的字符串
        String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.generatorQRCodeImage(payUrl, 350, 350, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/images/" + orderNo + ".png";
        return pngAddress;
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 查不到订单，报错
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.NOT_PAID.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    // 发货
    @Override
    public void deliver(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 查不到订单，报错
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        // 已付款-》已发货-》更新发货时间
        if (order.getOrderStatus() == Constant.OrderStatusEnum.PAID.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    // 完结
    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 查不到订单，报错
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        // 校验是普通用户, 需要校验订单所属
        if (!userService.checkAdminRole(UserFilter.currentUser) && !order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        // 发货后可以完结订单
        if (order.getOrderStatus() == Constant.OrderStatusEnum.DELIVERED.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

}
