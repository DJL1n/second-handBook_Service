package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.Message;
import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.MessageRepository;
import com.wjs.secondhandbook.repository.UserRepository;
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
    public String messagePage(@RequestParam(required = false) Integer withUser, Model model, Authentication auth) {
        User me = userRepository.findByUsername(auth.getName()).orElseThrow();

        // 1. 找出所有联系人 ID (合并 发给我的 和 我发给的)
        Set<Integer> contactIds = new HashSet<>();
        contactIds.addAll(messageRepository.findSenders(me.getUserId()));
        contactIds.addAll(messageRepository.findReceivers(me.getUserId()));

        // 2. 查出这些 User 对象
        Iterable<User> contactsIterable = userRepository.findAllById(contactIds);
        List<User> contacts = new ArrayList<>();
        contactsIterable.forEach(contacts::add);

        model.addAttribute("contacts", contacts);

        // 3. 如果指定了聊天对象 (withUser)，加载聊天记录
        if (withUser != null) {
            List<Message> history = messageRepository.findConversation(me.getUserId(), withUser);
            model.addAttribute("history", history);
            model.addAttribute("activeUserId", withUser);

            // 顺便把对方发给我的未读消息设为已读
            for(Message m : history) {
                if(m.getReceiverId().equals(me.getUserId()) && !m.getIsRead()) {
                    m.setIsRead(true);
                    messageRepository.save(m);
                }
            }

            // 获取当前聊天对象的信息
            userRepository.findById(withUser).ifPresent(u -> model.addAttribute("activeUser", u));
        }

        model.addAttribute("myId", me.getUserId());
        return "my_messages";
    }
}
