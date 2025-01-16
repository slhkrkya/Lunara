package com.example.Lunara.messaging_service.respository;

import com.example.Lunara.messaging_service.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRespository extends JpaRepository<Message, Long> {


    List<Message> findBySenderId(Long senderId);

    List<Message> findByReceiverId(Long receiverId);

}
