package it.unisa.biblionet.model.dao.chatbot;

import it.unisa.biblionet.model.entity.chatbot.ChatBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatBotDAO extends JpaRepository<ChatBot, Integer> {

}
