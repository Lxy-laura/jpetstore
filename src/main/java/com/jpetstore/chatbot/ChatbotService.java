package com.jpetstore.chatbot;

import com.jpetstore.domain.*;
import com.jpetstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderService orderService;

    // ==================== Intent Recognition ====================

    private String detectIntent(String message) {
        String msg = message.toLowerCase().trim();

        // Greetings
        if (msg.matches(".*(你好|您好|hi|hello|嗨|hey|早|晚上好|下午好|在吗|在不在).*")) return "greeting";

        // Farewells
        if (msg.matches(".*(再见|拜拜|bye|see you|下次|88).*")) return "farewell";

        // Thanks
        if (msg.matches(".*(谢谢|感谢|多谢|thank|thanks|3q).*")) return "thanks";

        // Order related
        if (msg.matches(".*(订单|下单|购买|买了|我的订单|订单状态|物流|配送).*")) return "order_query";

        // Product/price related
        if (msg.matches(".*(价格|多少钱|贵吗|便宜|打折|优惠|促销|特价|商品|产品|推荐|买什么).*")) return "product_info";

        // Inventory related
        if (msg.matches(".*(库存|有没有货|缺货|有货|现货|到货).*")) return "inventory_query";

        // Shipping info
        if (msg.matches(".*(运费|配送|快递|发货|包邮|物流).*")) return "shipping_info";

        // Return/refund
        if (msg.matches(".*(退货|退款|换货|售后|退换).*")) return "return_policy";

        // Account related
        if (msg.matches(".*(注册|登录|账号|密码|忘记密码|修改密码).*")) return "account_help";

        // Pet care / knowledge
        if (msg.matches(".*(怎么养|饲养|照顾|喂养|吃什么|喂什么|多久|怎么|可以|能|应该|需要|注意|疾病|生病|健康|疫苗|打针|驱虫|绝育|洗澡|美容|训练|教育).*")) return "pet_care";

        // Pet health
        if (msg.matches(".*(生病|病了|不吃|腹泻|呕吐|咳嗽|发烧|受伤|皮肤病|掉毛|驱虫|疫苗|体检).*")) return "pet_health";

        // Pet type specific
        if (msg.matches(".*(狗|犬|puppy|狗狗|金毛|哈士奇|泰迪|柯基|拉布拉多|德牧).*")) return "dog_care";
        if (msg.matches(".*(猫|猫咪|cat|kitten|布偶|英短|美短|暹罗|波斯猫|加菲).*")) return "cat_care";
        if (msg.matches(".*(鱼|fish|金鱼|锦鲤|热带鱼|龙鱼|观赏鱼|水族).*")) return "fish_care";
        if (msg.matches(".*(鸟|bird|鹦鹉|文鸟|百灵|画眉|雀|观赏鸟).*")) return "bird_care";
        if (msg.matches(".*(爬虫|爬行动物|蜥蜴|龟|蛇|reptile|守宫|鬃狮).*")) return "reptile_care";

        return "unknown";
    }

    // ==================== Response Generation ====================

    public ChatMessage generateResponse(String userMessage, List<ChatMessage> history) {
        String intent = detectIntent(userMessage);
        String response = generateByIntent(intent, userMessage, history);
        return new ChatMessage("assistant", response);
    }

    private String generateByIntent(String intent, String message, List<ChatMessage> history) {
        switch (intent) {
            case "greeting": return generateGreeting();
            case "farewell": return generateFarewell();
            case "thanks": return generateThanksResponse();
            case "order_query": return handleOrderQuery(message, history);
            case "product_info": return handleProductQuery(message);
            case "inventory_query": return handleInventoryQuery(message);
            case "shipping_info": return getShippingInfo();
            case "return_policy": return getReturnPolicy();
            case "account_help": return getAccountHelp();
            case "pet_care": return handlePetCareQuery(message);
            case "pet_health": return handlePetHealthQuery(message);
            case "dog_care": return getDogCareInfo(message);
            case "cat_care": return getCatCareInfo(message);
            case "fish_care": return getFishCareInfo(message);
            case "bird_care": return getBirdCareInfo(message);
            case "reptile_care": return getReptileCareInfo(message);
            case "unknown":
            default: return generateUnknownResponse();
        }
    }

    // ==================== Knowledge Base ====================

    private String generateGreeting() {
        String[] greetings = {
            "🐾 你好呀！我是小宠，你的专属宠物智能助手！有什么我可以帮你的吗？比如问问宠物怎么养、看看商品价格，或者查查订单状态都可以哦~",
            "👋 嗨！欢迎光临JPetStore~ 我是小宠，宠物知识小能手+客服小帮手，随时为你服务！",
            "😊 你好你好！我是小宠~ 无论是选宠物用品、了解饲养知识，还是查询订单，我都可以帮你搞定！",
            "🌟 欢迎来到宠物乐园！我是小宠，快告诉我你想了解什么吧~"
        };
        return greetings[new Random().nextInt(greetings.length)];
    }

    private String generateFarewell() {
        String[] farewells = {
            "👋 再见啦~ 希望小宠能帮到你！下次想了解宠物知识随时找我哦！",
            "😊 bye bye~ 祝你和小宠物都开心每一天！记得常来玩~",
            "🌸 再见！好好照顾你的小可爱，有任何问题随时来找小宠聊天！"
        };
        return farewells[new Random().nextInt(farewells.length)];
    }

    private String generateThanksResponse() {
        String[] thanks = {
            "😊 不客气！能帮到你是小宠最大的快乐~ 还有其它问题随时问我哦！",
            "💪 客气啦！小宠随时待命，有问题尽管找我！",
            "🌟 举手之劳啦~ 希望你和宠物都健康快乐！"
        };
        return thanks[new Random().nextInt(thanks.length)];
    }

    private String handleOrderQuery(String message, List<ChatMessage> history) {
        Pattern orderIdPattern = Pattern.compile("(\\d+)");
        Matcher matcher = orderIdPattern.matcher(message);

        if (matcher.find()) {
            String orderIdStr = matcher.group(1);
            try {
                Integer orderId = Integer.parseInt(orderIdStr);
                Order order = orderService.getOrderById(orderId);
                if (order != null) {
                    String statusText = getOrderStatusText(order.getStatus());
                    return "📦 订单 #" + orderId + " 的信息如下：\n" +
                           "状态：" + statusText + "\n" +
                           "金额：¥" + order.getTotalprice() + "\n" +
                           "日期：" + order.getOrderdate() + "\n" +
                           "收货人：" + order.getShiptofirstname() + " " + order.getShiptolastname();
                } else {
                    return "😅 抱歉，我查不到订单 #" + orderId + " 的信息，请确认订单号是否正确哦~";
                }
            } catch (NumberFormatException e) {
                return "😅 订单号格式不对哦，请输入纯数字订单编号~";
            }
        }

        return "📋 关于订单的问题，你可以：\n" +
               "1️⃣ 直接告诉我订单号，我帮你查状态\n" +
               "2️⃣ 登录后在「我的订单」查看所有订单\n" +
               "3️⃣ 需要其他帮助也可以问我哦~";
    }

    private String handleProductQuery(String message) {
        // Try to find a product name or keyword
        List<Product> products = productService.getAllProducts();
        List<Product> matched = new ArrayList<>();

        for (Product p : products) {
            if (p.getName() != null && message.contains(p.getName().substring(0, Math.min(2, p.getName().length())))) {
                matched.add(p);
            } else if (p.getCategory() != null && message.toUpperCase().contains(p.getCategory())) {
                matched.add(p);
            }
        }

        if (!matched.isEmpty()) {
            StringBuilder sb = new StringBuilder("🎯 找到以下相关商品：\n");
            for (int i = 0; i < Math.min(3, matched.size()); i++) {
                Product p = matched.get(i);
                sb.append("• ").append(p.getName())
                  .append(" — ¥").append(p.getPrice() != null ? p.getPrice() : "待定")
                  .append("\n");
            }
            sb.append("\n在商品页面可以查看详情和下单购买哦~");
            return sb.toString();
        }

        return "🛍️ 我们店里有各类宠物商品哦！\n" +
               "• 🐟 鱼类宠物及用品\n" +
               "• 🐕 狗狗宠物及用品\n" +
               "• 🐈 猫咪宠物及用品\n" +
               "• 🦎 爬行动物及用品\n" +
               "• 🐦 鸟类宠物及用品\n\n" +
               "你对哪类宠物感兴趣呀？我帮你推荐~";
    }

    private String handleInventoryQuery(String message) {
        return "📦 你可以直接在商品页面查看实时库存数量哦！\n" +
               "如果商品显示「有货」就可以放心下单购买啦~ 🛒\n" +
               "如果显示缺货，可以过段时间再来看看，我们会及时补货的！";
    }

    private String getShippingInfo() {
        return "🚚 配送信息：\n" +
               "• 发货时间：工作日下单后24小时内发货\n" +
               "• 配送时效：同城1-2天，跨省3-5天\n" +
               "• 满¥199包邮\n" +
               "• 支持顺丰、中通、圆通等快递\n\n" +
               "💡 活体宠物我们会采用专业宠物运输箱，确保安全送达！";
    }

    private String getReturnPolicy() {
        return "🔄 退换货政策：\n" +
               "• 宠物用品：7天无理由退换（不影响二次销售）\n" +
               "• 活体宠物：签收后24小时内健康问题可联系售后\n" +
               "• 食品类：非质量问题不退换\n\n" +
               "如有问题联系客服：support@jpetstore.com\n" +
               "或致电：400-888-8888";
    }

    private String getAccountHelp() {
        return "👤 账号帮助：\n" +
               "• 注册：点击右上角「注册」，填写信息即可\n" +
               "• 登录：使用用户名和密码登录\n" +
               "• 忘记密码：请联系客服 reset@jpetstore.com 重置\n" +
               "• 修改信息：登录后在「个人中心」修改";
    }

    private String handlePetCareQuery(String message) {
        if (message.contains("狗") || message.contains("犬")) return getDogCareInfo(message);
        if (message.contains("猫")) return getCatCareInfo(message);
        if (message.contains("鱼")) return getFishCareInfo(message);
        if (message.contains("鸟")) return getBirdCareInfo(message);
        if (message.contains("龟") || message.contains("蜥蜴") || message.contains("蛇")) return getReptileCareInfo(message);

        return "🐾 养宠物可是件快乐又需要用心的事呢！\n" +
               "你想了解哪种宠物的饲养知识呀？\n" +
               "🐕 狗狗  🐈 猫咪  🐟 鱼类  🐦 鸟类  🦎 爬行动物\n\n" +
               "可以直接告诉我，比如「怎么养狗」「猫咪吃什么」之类的~";
    }

    private String handlePetHealthQuery(String message) {
        if (message.contains("狗") || message.contains("犬")) {
            return "🏥 狗狗常见健康问题：\n\n" +
                   "⚠️ 呕吐/腹泻：可能是饮食不当或肠胃炎，禁食12-24小时后喂易消化食物\n" +
                   "⚠️ 皮肤病：瘙痒、掉毛可能是真菌或寄生虫感染，需就医\n" +
                   "⚠️ 疫苗接种：幼犬需打3针联苗+1针狂犬疫苗\n" +
                   "⚠️ 定期驱虫：每3个月一次体内驱虫，每月一次体外驱虫\n\n" +
                   "💡 建议每年带狗狗做一次全面体检！";
        }
        if (message.contains("猫")) {
            return "🏥 猫咪常见健康问题：\n\n" +
                   "⚠️ 猫瘟/猫鼻支：需及时接种疫苗预防\n" +
                   "⚠️ 尿闭：公猫常见，注意观察排尿\n" +
                   "⚠️ 口腔问题：定期刷牙，预防口炎\n" +
                   "⚠️ 驱虫：每月体外驱虫，3个月一次体内驱虫\n\n" +
                   "💡 猫咪很能忍痛，发现异常要及时就医哦！";
        }
        return "💊 宠物健康小贴士：\n" +
               "• 定期接种疫苗很重要\n" +
               "• 发现异常及时就医别拖延\n" +
               "• 日常观察食欲、精神、排便情况\n\n" +
               "能告诉我具体是什么宠物吗？我提供更详细的建议~";
    }

    private String getDogCareInfo(String message) {
        return "🐕 狗狗饲养小课堂 🐕\n\n" +
               "🏠 环境：准备舒适狗窝，保持通风干净\n" +
               "🍖 饮食：幼犬一天3-4餐，成犬一天2餐\n" +
               "💉 疫苗：幼犬6-8周开始接种，共3针联苗+狂犬疫苗\n" +
               "🚶 运动：每天至少遛30分钟，大型犬需要更多运动\n" +
               "🛁 洗澡：夏天7-10天一次，冬天2周一次\n" +
               "✂️ 美容：定期修剪指甲、清理耳朵\n\n" +
               "💡 狗狗是群居动物，要多陪伴它哦！";
    }

    private String getCatCareInfo(String message) {
        return "🐈 猫咪饲养小课堂 🐈\n\n" +
               "🏠 环境：准备猫砂盆、猫抓板、猫爬架\n" +
               "🍖 饮食：猫是肉食动物，选高蛋白猫粮\n" +
               "💉 疫苗：猫三联+狂犬疫苗，每年加强\n" +
               "🚽 猫砂：每天清理，每周彻底更换\n" +
               "🛁 洗澡：猫会自己清洁，半年洗一次即可\n" +
               "✂️ 绝育：建议6-8个月大时做绝育\n\n" +
               "💡 猫咪需要垂直空间，准备猫爬架它会很开心！";
    }

    private String getFishCareInfo(String message) {
        return "🐟 观赏鱼饲养小课堂 🐟\n\n" +
               "🏠 鱼缸：新手建议从中型缸开始（60cm以上）\n" +
               "🌡️ 水温：热带鱼24-28℃，金鱼18-24℃\n" +
               "💧 换水：每周换1/3，晾晒或除氯后的水\n" +
               "🍖 喂食：每天1-2次，3分钟内吃完的量\n" +
               "🌿 造景：提供躲避处，不要过于拥挤\n\n" +
               "💡 新鱼入缸前要过水适应，养鱼先养水！";
    }

    private String getBirdCareInfo(String message) {
        return "🐦 观赏鸟饲养小课堂 🐦\n\n" +
               "🏠 鸟笼：选择足够大的笼子，直径至少是鸟的2倍翼展\n" +
               "🍖 饮食：以专用鸟粮为主，搭配新鲜蔬果\n" +
               "🛁 洗澡：提供浅水盆让鸟自己洗浴\n" +
               "🎵 互动：多和鸟说话、玩耍，会变得更亲人\n" +
               "✂️ 剪羽：安全考虑可适当修剪飞羽\n\n" +
               "💡 鹦鹉是社交性动物，需要主人每天陪伴互动！";
    }

    private String getReptileCareInfo(String message) {
        return "🦎 爬行动物饲养小课堂 🦎\n\n" +
               "🏠 饲养箱：根据不同种类设置温湿度梯度\n" +
               "🌡️ 温度：需要热点区（晒太阳）和凉区\n" +
               "💡 光照：UVB灯对钙质吸收至关重要\n" +
               "🍖 饮食：多数吃昆虫或蔬菜，不同种类差异大\n" +
               "💧 湿度：保持适当湿度，定期喷水\n\n" +
               "💡 养爬宠前一定要先了解该品种的特定需求哦！";
    }

    private String generateUnknownResponse() {
        String[] responses = {
            "🤔 小宠还在学习中~ 你可以问我这些问题：\n" +
            "• 宠物怎么养（如「怎么养狗」「猫咪吃什么」）\n" +
            "• 商品信息（如「有什么猫粮推荐」）\n" +
            "• 订单查询（告诉我订单号）\n" +
            "• 配送和售后政策",

            "😅 这个问题有点难到小宠了~ 不过我可以帮你：\n" +
            "🐾 了解宠物饲养知识\n" +
            "🛍️ 推荐宠物商品\n" +
            "📦 查询订单状态\n" +
            "💬 回答常见问题\n\n" +
            "换个方式问问看？",

            "💡 试试这样问我吧：\n" +
            "• 「金毛怎么养」— 了解宠物饲养\n" +
            "• 「有什么狗狗推荐」— 商品推荐\n" +
            "• 「我的订单#1001」— 查询订单\n" +
            "• 「配送几天到」— 物流信息"
        };
        return responses[new Random().nextInt(responses.length)];
    }

    // ==================== Category Knowledge Suggestions ====================

    public List<String> getCategoryKnowledgeTips(String categoryId) {
        Map<String, List<String>> tips = new HashMap<>();
        tips.put("FISH", Arrays.asList(
            "🐟 养鱼先养水：新缸需循环7-14天才能放鱼",
            "🐟 每周换水1/3，不要全部换掉",
            "🐟 喂食量以3分钟内吃完为宜"
        ));
        tips.put("DOGS", Arrays.asList(
            "🐕 幼犬每天喂3-4餐，成犬每天2餐",
            "🐕 每天至少遛狗30分钟",
            "🐕 定期接种疫苗和驱虫很重要"
        ));
        tips.put("CATS", Arrays.asList(
            "🐈 猫是纯肉食动物，需要高蛋白饮食",
            "🐈 每天清理猫砂盆，猫咪爱干净",
            "🐈 建议6-8个月大时做绝育"
        ));
        tips.put("REPTILES", Arrays.asList(
            "🦎 UVB灯对爬虫钙质吸收至关重要",
            "🦎 设置温度梯度：热点和凉区",
            "🦎 不同品种湿度需求差异大"
        ));
        tips.put("BIRDS", Arrays.asList(
            "🐦 鹦鹉需要主人每天陪伴互动",
            "🐦 笼子要足够大，提供玩具",
            "🐦 食物以专用鸟粮为主，搭配蔬果"
        ));
        return tips.getOrDefault(categoryId, Collections.emptyList());
    }

    private String getOrderStatusText(String status) {
        switch (status != null ? status : "") {
            case "P": return "⏳ 待处理";
            case "A": return "✅ 已确认";
            case "S": return "📤 已发货";
            case "C": return "🎉 已完成";
            default: return "❓ 未知状态";
        }
    }
}
