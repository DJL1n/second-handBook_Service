package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.Message;
import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.MessageRepository;
import com.wjs.secondhandbook.repository.ProductRepository;
import com.wjs.secondhandbook.repository.UserRepository;
import com.wjs.secondhandbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MessageController {

    @Autowired private MessageRepository messageRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;

    /**
     * API: 发送消息
     */
    @ResponseBody
    @PostMapping("/api/message/send")
    public Map<String, Object> sendMessage(@RequestBody Map<String, Object> payload, Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        try {
            User me = userRepository.findByUsername(auth.getName()).orElseThrow();
            Integer receiverId = Integer.parseInt(payload.get("receiverId").toString());
            String content = (String) payload.get("content");
            Integer productId = payload.containsKey("productId") ? Integer.parseInt(payload.get("productId").toString()) : null;

            if(me.getUserId().equals(receiverId)) {
                throw new RuntimeException("不能给自己发消息");
            }

            Message msg = new Message();
            msg.setSenderId(me.getUserId());
            msg.setReceiverId(receiverId);
            msg.setContent(content);
            msg.setProductId(productId);
            msg.setCreatedAt(LocalDateTime.now());
            msg.setIsRead(false);

            messageRepository.save(msg);
            res.put("success", true);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }
        return res;
    }

    /**
     * API: 获取未读数量 (用于 Navbar 红点提醒)
     */
    @ResponseBody
    @GetMapping("/api/message/unread-count")
    public Map<String, Object> getUnreadCount(Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        if (auth == null) {
            res.put("count", 0);
            return res;
        }
        User me = userRepository.findByUsername(auth.getName()).orElse(null);
        if(me != null) {
            res.put("count", messageRepository.countByReceiverIdAndIsReadFalse(me.getUserId()));
        } else {
            res.put("count", 0);
        }
        return res;
    }

    /**
     * 页面: 消息中心
     * logic: 找出所有和我聊过天的人，展示列表
     */
    @GetMapping("/my-messages")
    public String messagePage(@RequestParam(required = false) Integer withUser,
                              @RequestParam(required = false) Integer productId, // 🔥 2. 允许前端显式传 productId
                              Model model, Authentication auth) {
        User me = userRepository.findByUsername(auth.getName()).orElseThrow();

        // 1. 找出所有联系人 (保持不变)
        Set<Integer> contactIds = new HashSet<>();
        contactIds.addAll(messageRepository.findSenders(me.getUserId()));
        contactIds.addAll(messageRepository.findReceivers(me.getUserId()));
        Iterable<User> contactsIterable = userRepository.findAllById(contactIds);
        List<User> contacts = new ArrayList<>();
        contactsIterable.forEach(contacts::add);
        model.addAttribute("contacts", contacts);

        // 🔥🔥🔥 新增逻辑开始：计算每个联系人的未读数
        Map<Integer, Long> unreadCounts = new HashMap<>();
        for (User contact : contacts) {
            // 查出这个联系人(contact.userId)发给我(me.getUserId())的未读数
            Long count = messageRepository.countUnread(contact.getUserId(), me.getUserId());
            unreadCounts.put(contact.getUserId(), count);
        }
        model.addAttribute("unreadCounts", unreadCounts); // 放入 Model 传给前端
        // 🔥🔥🔥 新增逻辑结束


        // 2. 如果指定了聊天对象
        if (withUser != null) {
            List<Message> history = messageRepository.findConversation(me.getUserId(), withUser);
            model.addAttribute("history", history);
            model.addAttribute("activeUserId", withUser);

            // 获取当前聊天对象信息
            userRepository.findById(withUser).ifPresent(u -> model.addAttribute("activeUser", u));

            // 设为已读 (保持不变)
            for(Message m : history) {
                if(m.getReceiverId().equals(me.getUserId()) && !m.getIsRead()) {
                    m.setIsRead(true);
                    messageRepository.save(m);
                }
            }

            // 获取当前聊天对象信息
            userRepository.findById(withUser).ifPresent(u -> model.addAttribute("activeUser", u));

            // 🔥 3. 核心逻辑：查找关联商品
            // 优先级 A: URL 参数指定了 productId (例如从详情页跳转过来)
            // 优先级 B: 聊天记录中最新的那条带 productId 的消息
            Integer targetProductId = productId;

            if (targetProductId == null && !history.isEmpty()) {
                // 倒序遍历历史记录，找到最近一个有关联商品的消息
                for (int i = history.size() - 1; i >= 0; i--) {
                    if (history.get(i).getProductId() != null) {
                        targetProductId = history.get(i).getProductId();
                        break;
                    }
                }
            }

            // 如果找到了商品ID，查出详情放进 Model
            if (targetProductId != null) {
                productRepository.findById(targetProductId).ifPresent(p -> {
                    model.addAttribute("currentProduct", p);
                });
            }
        }

        model.addAttribute("myId", me.getUserId());
        return "my_messages";
    }
}
