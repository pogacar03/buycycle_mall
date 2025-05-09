package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.utils.TokenUtils;
import com.example.utils.UserCF;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.management.relation.Role;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 公告信息表业务处理
 **/
@Service
public class GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CollectMapper collectMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private OrdersMapper ordersMapper;

    /**
     * 新增
     */
    public void add(Goods goods) {

        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            goods.setBusinessId(currentUser.getId());
        }

        goodsMapper.insert(goods);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        goodsMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            goodsMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Goods goods) {
        goodsMapper.updateById(goods);
    }

    /**
     * 根据ID查询
     */
    public Goods selectById(Integer id) {
        return goodsMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Goods> selectAll(Goods goods) {
        return goodsMapper.selectAll(goods);
    }

    /**
     * 分页查询
     */
    public PageInfo<Goods> selectPage(Goods goods, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            goods.setBusinessId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsMapper.selectAll(goods);
        return PageInfo.of(list);
    }

    public List<Goods> selectTop15() {

        return goodsMapper.selectTop15();
    }

    public List<Goods> selectByTypeId(Integer id) {
        return goodsMapper.selectByTypeId(id);
    }

    public List<Goods> selectByBusinessId(Integer id) {
        return goodsMapper.selectByBusinessId(id);
    }

    public List<Goods> selectByName(String name) {
        return goodsMapper.selectByName(name);
    }

    public List<Goods> recommend() {
        Account currentUser = TokenUtils.getCurrentUser();
        if (ObjectUtil.isEmpty(currentUser)) {
            // 没有用户登录
            return new ArrayList<>();
        }
        // 用户的哪些行为可以认为他跟商品产生了关系？收藏、加入购物车、下单、评论
        // 1. 获取所有的收藏信息
        List<Collect> allCollects = collectMapper.selectAll(null);
        // 2. 获取所有的购物车信息
        List<Cart> allCarts = cartMapper.selectAll(null);
        // 3. 获取所有的订单信息
        List<Orders> allOrders = ordersMapper.selectAllOKOrders();
        // 4. 获取所有的评论信息
        List<Comment> allComments = commentMapper.selectAll(null);
        // 5. 获取所有的用户信息
        List<User> allUsers = userMapper.selectAll(null);
        // 6. 获取所有的商品信息
        List<Goods> allGoods = goodsMapper.selectAll(null);

        // 定义一个存储每个商品和每个用户关系的List
        List<RelateDTO> data = new ArrayList<>();
        // 定义一个存储最后返回给前端的商品List
        List<Goods> recommendResult;
        // 创建一个栅栏，等待所有的异步处理都结束后，再往下走
        int totalTasks = allCollects.size() * allUsers.size();
        if (totalTasks <= 0)
            totalTasks = 1; // 确保有至少一个任务
        CountDownLatch countDownLatch = new CountDownLatch(totalTasks);
        // 创建一个线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();

        // 开始计算每个商品和每个用户之间的关系数据
        for (Goods goods : allGoods) {
            if (goods == null || goods.getId() == null)
                continue;

            final Integer goodsId = goods.getId();
            for (User user : allUsers) {
                if (user == null || user.getId() == null)
                    continue;

                final Integer userId = user.getId();
                threadPool.execute(() -> {
                    try {
                        int index = 1;
                        // 1. 判断该用户有没有收藏该商品，收藏的权重我们给 1
                        Optional<Collect> collectOptional = allCollects.stream()
                                .filter(x -> x != null && x.getGoodsId() != null && x.getUserId() != null
                                        && x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId))
                                .findFirst();
                        if (collectOptional.isPresent()) {
                            index += 1;
                        }
                        // 2. 判断该用户有没有给该商品加入购物车，加入购物车的权重我们给 2
                        Optional<Cart> cartOptional = allCarts.stream()
                                .filter(x -> x != null && x.getGoodsId() != null && x.getUserId() != null
                                        && x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId))
                                .findFirst();
                        if (cartOptional.isPresent()) {
                            index += 2;
                        }
                        // 3. 判断该用户有没有对该商品下过单（已完成的订单），订单的权重我们给 3
                        Optional<Orders> ordersOptional = allOrders.stream()
                                .filter(x -> x != null && x.getGoodsId() != null && x.getUserId() != null
                                        && x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId))
                                .findFirst();
                        if (ordersOptional.isPresent()) {
                            index += 3;
                        }
                        // 4. 判断该用户有没有对该商品评论过，评论的权重我们给 2
                        Optional<Comment> commentOptional = allComments.stream()
                                .filter(x -> x != null && x.getGoodsId() != null && x.getUserId() != null
                                        && x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId))
                                .findFirst();
                        if (commentOptional.isPresent()) {
                            index += 2;
                        }
                        if (index > 1) {
                            synchronized (data) {
                                data.add(new RelateDTO(userId, goodsId, index));
                            }
                        }
                    } catch (Exception e) {
                        // 防止线程池异常中断
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
        }

        try {
            // 等待任务完成或超时
            threadPool.shutdown();
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 数据准备结束后，就把这些数据一起喂给这个推荐算法
            List<Integer> goodsIds = UserCF.recommend(currentUser.getId(), data);
            if (goodsIds == null) {
                goodsIds = new ArrayList<>();
            }

            // 把商品id转换成商品
            recommendResult = goodsIds.stream()
                    .filter(goodsId -> goodsId != null)
                    .map(goodsId -> allGoods.stream()
                            .filter(x -> x != null && x.getId() != null && x.getId().equals(goodsId))
                            .findFirst()
                            .orElse(null))
                    .filter(g -> g != null)
                    .limit(10)
                    .collect(Collectors.toList());
        }

        // 如果推荐结果为空或不足10个，使用随机推荐补充
        if (recommendResult == null) {
            recommendResult = getRandomGoods(10);
        } else if (recommendResult.size() < 10) {
            // 获取已推荐商品的ID集合，用于去重
            Set<Integer> existingGoodsIds = recommendResult.stream()
                    .map(Goods::getId)
                    .collect(Collectors.toSet());

            int num = 10 - recommendResult.size();
            // 获取更多的随机商品
            List<Goods> randomGoods = getRandomGoods(num * 2);

            // 过滤掉已经存在的商品ID
            List<Goods> filteredRandomGoods = randomGoods.stream()
                    .filter(g -> g != null && g.getId() != null && !existingGoodsIds.contains(g.getId()))
                    .limit(num)
                    .collect(Collectors.toList());

            recommendResult.addAll(filteredRandomGoods);
        }

        // 最后确保没有重复
        return recommendResult.stream()
                .filter(g -> g != null && g.getId() != null)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Goods::getId, g -> g, (g1, g2) -> g1),
                        map -> new ArrayList<>(map.values())));
    }

    private List<Goods> getRandomGoods(int num) {
        List<Goods> list = new ArrayList<>(num);
        List<Goods> goods = goodsMapper.selectAll(null);

        if (goods == null || goods.isEmpty() || num <= 0) {
            return list;
        }

        // 确保不超过可用的商品数
        num = Math.min(num, goods.size());

        // 使用Set存储已选择的索引，避免重复选择
        Set<Integer> selectedIndices = new HashSet<>();

        // 尝试选择不重复的商品
        for (int i = 0; i < num; i++) {
            try {
                // 尝试最多10次找到一个未被选择的索引
                int maxAttempts = 10;
                int attempt = 0;
                int index;

                do {
                    index = new Random().nextInt(goods.size());
                    attempt++;
                } while (selectedIndices.contains(index) && attempt < maxAttempts);

                // 如果尝试多次后仍找不到新索引，则跳过
                if (selectedIndices.contains(index)) {
                    continue;
                }

                selectedIndices.add(index);
                Goods item = goods.get(index);
                if (item != null) {
                    list.add(item);
                }
            } catch (Exception e) {
                // 防止随机数异常
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 更新商品库存
     * 
     * @param goodsId  商品ID
     * @param quantity 变更数量（正数增加，负数减少）
     * @return 是否更新成功
     */
    public boolean updateStock(Integer goodsId, Integer quantity) {
        try {
            Goods goods = goodsMapper.selectById(goodsId);
            if (goods == null) {
                return false;
            }

            // 如果是减库存操作，检查库存是否足够
            if (quantity < 0 && (goods.getStock() == null || goods.getStock() < Math.abs(quantity))) {
                return false;
            }

            // 更新库存
            Integer newStock = goods.getStock() == null ? quantity : goods.getStock() + quantity;
            goods.setStock(newStock < 0 ? 0 : newStock);
            goodsMapper.updateById(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查商品库存是否充足
     * 
     * @param goodsId  商品ID
     * @param quantity 需要的数量
     * @return 是否充足
     */
    public boolean checkStock(Integer goodsId, Integer quantity) {
        Goods goods = goodsMapper.selectById(goodsId);
        return goods != null && goods.getStock() != null && goods.getStock() >= quantity;
    }
}