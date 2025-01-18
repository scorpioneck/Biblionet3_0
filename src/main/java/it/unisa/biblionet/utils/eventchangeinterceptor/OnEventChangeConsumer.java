package it.unisa.biblionet.utils.eventchangeinterceptor;

import it.unisa.biblionet.model.entity.Evento;

import java.util.function.Consumer;

/**
 * Interfaccia.
 */
public interface OnEventChangeConsumer extends Consumer<Evento> { }
