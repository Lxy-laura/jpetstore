package com.jpetstore.controller;

import com.jpetstore.domain.Account;
import com.jpetstore.domain.Cart;
import com.jpetstore.domain.Category;
import com.jpetstore.domain.Item;
import com.jpetstore.domain.Order;
import com.jpetstore.domain.OrderItem;
import com.jpetstore.domain.Product;
import com.jpetstore.service.AccountService;
import com.jpetstore.service.CategoryService;
import com.jpetstore.service.ItemService;
import com.jpetstore.service.OrderService;
import com.jpetstore.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PageController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class})
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;
    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private AccountService accountService;
    @MockitoBean
    private OrderService orderService;

    private Account testUser;
    private Account adminUser;
    private MockHttpSession userSession;
    private MockHttpSession adminSession;

    @BeforeEach
    void setUp() {
        testUser = new Account();
        testUser.setUserid("j2ee");
        testUser.setAdmin(false);

        adminUser = new Account();
        adminUser.setUserid("admin");
        adminUser.setAdmin(true);

        userSession = new MockHttpSession();
        userSession.setAttribute("user", testUser);

        adminSession = new MockHttpSession();
        adminSession.setAttribute("user", adminUser);
    }

    @Test
    void testIndexPage() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("categories", "products"));
    }

    @Test
    void testCategoryPage() throws Exception {
        Category cat = new Category();
        cat.setCatid("FISH");
        cat.setName("鱼类");
        when(categoryService.getCategoryById("FISH")).thenReturn(cat);
        when(productService.getProductsByCategory("FISH")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/category/FISH"))
                .andExpect(status().isOk())
                .andExpect(view().name("category"))
                .andExpect(model().attribute("category", cat));
    }

    @Test
    void testProductPage() throws Exception {
        Product product = new Product();
        product.setProductid("P001");
        product.setName("神仙鱼");
        List<Item> items = Collections.emptyList();
        when(productService.getProductById("P001")).thenReturn(product);
        when(itemService.getItemsByProductId("P001")).thenReturn(items);
        mockMvc.perform(get("/product/P001"))
                .andExpect(status().isOk())
                .andExpect(view().name("product"))
                .andExpect(model().attribute("product", product));
    }

    @Test
    void testProductPageNullProduct() throws Exception {
        when(productService.getProductById("P999")).thenReturn(null);
        mockMvc.perform(get("/product/P999"))
                .andExpect(status().isOk())
                .andExpect(view().name("product"))
                .andExpect(model().attribute("product", (Object) null));
    }

    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void testCartPageNoSession() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cart"));
    }

    @Test
    void testCartPageWithExistingCart() throws Exception {
        Cart cart = new Cart();
        userSession.setAttribute("cart", cart);
        mockMvc.perform(get("/cart").session(userSession))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"));
    }

    @Test
    void testCheckoutPageSuccess() throws Exception {
        Cart cart = new Cart();
        Item item = new Item();
        item.setItemid("I001");
        cart.addItem(item);
        userSession.setAttribute("cart", cart);
        mockMvc.perform(get("/checkout").session(userSession))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"));
    }

    @Test
    void testCheckoutPageNotLoggedIn() throws Exception {
        mockMvc.perform(get("/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testCheckoutPageNullCart() throws Exception {
        userSession.setAttribute("cart", null);
        mockMvc.perform(get("/checkout").session(userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    void testCheckoutPageEmptyCart() throws Exception {
        Cart cart = new Cart();
        userSession.setAttribute("cart", cart);
        mockMvc.perform(get("/checkout").session(userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    void testOrdersPageSuccess() throws Exception {
        when(orderService.getOrdersByUserId("j2ee")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/orders").session(userSession))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"));
    }

    @Test
    void testOrdersPageNotLoggedIn() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testOrderDetailPageSuccess() throws Exception {
        Order order = new Order();
        order.setOrderid(1);
        List<OrderItem> items = Collections.emptyList();
        when(orderService.getOrderById(1)).thenReturn(order);
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(items);
        mockMvc.perform(get("/order/1").session(userSession))
                .andExpect(status().isOk())
                .andExpect(view().name("order-detail"));
    }

    @Test
    void testOrderDetailPageNullOrder() throws Exception {
        when(orderService.getOrderById(999)).thenReturn(null);
        mockMvc.perform(get("/order/999").session(userSession))
                .andExpect(status().isOk())
                .andExpect(view().name("order-detail"));
    }

    @Test
    void testOrderDetailPageNotLoggedIn() throws Exception {
        mockMvc.perform(get("/order/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testProfilePageSuccess() throws Exception {
        mockMvc.perform(get("/profile").session(userSession))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));
    }

    @Test
    void testProfilePageNotLoggedIn() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testSearchPage() throws Exception {
        when(productService.searchProducts("dog")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/search").param("keyword", "dog"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("keyword", "dog"));
    }

    @Test
    void testAdminPageAsAdmin() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());
        when(accountService.getAllUsers()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/admin").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andExpect(model().attribute("categoryCount", 0))
                .andExpect(model().attribute("productCount", 0))
                .andExpect(model().attribute("orderCount", 0))
                .andExpect(model().attribute("userCount", 0));
    }

    @Test
    void testAdminPageAsNormalUser() throws Exception {
        mockMvc.perform(get("/admin").session(userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testAdminPageNotLoggedIn() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}