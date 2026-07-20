package com.jpetstore.service;

import com.jpetstore.domain.Cart;
import com.jpetstore.domain.CartItem;
import com.jpetstore.domain.Product;
import com.jpetstore.domain.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private static final String VIEW_HISTORY_KEY = "view_history";

    /**
     * 获取基于浏览历史的推荐
     */
    public List<Product> getRecommendations(HttpSession session, int limit) {
        List<Product> result = new ArrayList<>();
        Set<String> excludedIds = new HashSet<>();

        // 1. Exclude items already in cart
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            for (CartItem ci : cart.getCartItems()) {
                if (ci.getItem() != null && ci.getItem().getProduct() != null) {
                    excludedIds.add(ci.getItem().getProduct().getProductid());
                }
            }
        }

        // 2. Exclude items already in wishlist
        Wishlist wishlist = (Wishlist) session.getAttribute("wishlist");
        if (wishlist != null) {
            excludedIds.addAll(wishlist.getProducts().stream()
                    .map(Product::getProductid).collect(Collectors.toSet()));
        }

        // 3. Get browse history
        @SuppressWarnings("unchecked")
        List<Product> viewHistory = (List<Product>) session.getAttribute(VIEW_HISTORY_KEY);

        if (viewHistory != null && !viewHistory.isEmpty()) {
            // Recommend from same categories as viewed products
            Map<String, Long> categoryCount = viewHistory.stream()
                    .filter(p -> p.getCategory() != null)
                    .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

            // Sort categories by view count
            List<String> topCategories = categoryCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            for (String catId : topCategories) {
                if (result.size() >= limit) break;
                List<Product> catProducts = productService.getProductsByCategory(catId);
                Collections.shuffle(catProducts);
                for (Product p : catProducts) {
                    if (!excludedIds.contains(p.getProductid()) && result.size() < limit) {
                        // Make sure it's not already in view history
                        boolean alreadyViewed = viewHistory.stream()
                                .anyMatch(vp -> vp.getProductid().equals(p.getProductid()));
                        if (!alreadyViewed) {
                            result.add(p);
                            excludedIds.add(p.getProductid());
                        }
                    }
                }
            }
        }

        // 4. Fallback: random products
        if (result.size() < limit) {
            List<Product> allProducts = productService.getAllProducts()
                    .stream().filter(p -> !excludedIds.contains(p.getProductid()))
                    .collect(Collectors.toList());
            Collections.shuffle(allProducts);
            for (Product p : allProducts) {
                if (result.size() >= limit) break;
                result.add(p);
            }
        }

        return result;
    }

    /**
     * 获取"购买此商品的用户也买了"的推荐
     */
    public List<Product> getRelatedProducts(String productId, int limit) {
        Product product = productService.getProductById(productId);
        if (product == null) return Collections.emptyList();

        List<Product> related = productService.getProductsByCategory(product.getCategory())
                .stream()
                .filter(p -> !p.getProductid().equals(productId))
                .limit(limit)
                .collect(Collectors.toList());

        // If not enough, add random products
        if (related.size() < limit) {
            List<Product> others = productService.getAllProducts()
                    .stream()
                    .filter(p -> !p.getProductid().equals(productId))
                    .filter(p -> related.stream().noneMatch(r -> r.getProductid().equals(p.getProductid())))
                    .limit(limit - related.size())
                    .collect(Collectors.toList());
            related.addAll(others);
        }

        return related;
    }

    /**
     * 记录浏览历史
     */
    public void recordView(HttpSession session, Product product) {
        @SuppressWarnings("unchecked")
        List<Product> history = (List<Product>) session.getAttribute(VIEW_HISTORY_KEY);
        if (history == null) {
            history = new ArrayList<>();
        }

        // Remove if already exists (to move to front)
        history.removeIf(p -> p.getProductid().equals(product.getProductid()));
        history.add(0, product); // Add to front (most recent)

        // Keep max 20 items
        if (history.size() > 20) {
            history = history.subList(0, 20);
        }

        session.setAttribute(VIEW_HISTORY_KEY, history);
    }

    /**
     * 获取最近浏览的商品
     */
    public List<Product> getRecentViews(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Product> history = (List<Product>) session.getAttribute(VIEW_HISTORY_KEY);
        return history != null ? history : Collections.emptyList();
    }
}
