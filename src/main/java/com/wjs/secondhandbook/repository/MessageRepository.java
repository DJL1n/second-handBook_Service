package com.wjs.secondhandbook.repository;

import com.wjs.secondhandbook.model.Message;
import org.springframework.data.jdbc.repository.query.Query; // ⚠️ JDBC 的 Query 注解
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {

    // 1. 查找对话记录 (使用原生 SQL)
    @Query("SELECT * FROM messages WHERE (sender_id = :myId AND receiver_id = :partnerId) OR (sender_id = :partnerId AND receiver_id = :myId) ORDER BY created_at ASC")
    List<Message> findConversation(Integer myId, Integer partnerId);

    // 2. 统计未读消息 (Spring Data JDBC 支持方法名自动生成查询，这行不用改)
    Integer countByReceiverIdAndIsReadFalse(Integer userId);

    // 3. 查找联系人列表 (使用原生 SQL)
    @Query("SELECT DISTINCT sender_id FROM messages WHERE receiver_id = :myId")
    List<Integer> findSenders(Integer myId);

    @Query("SELECT DISTINCT receiver_id FROM messages WHERE sender_id = :myId")
    List<Integer> findReceivers(Integer myId);
}
