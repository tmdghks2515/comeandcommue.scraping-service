package io.comeandcommue.scraping.common;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.security.SecureRandom;
import java.util.EnumSet;

public class NanoIdGenerator implements BeforeExecutionGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] ALPHABET = NanoIdUtils.DEFAULT_ALPHABET;
    private static final int ID_LENGTH = 11;

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        return NanoIdUtils.randomNanoId(RANDOM, ALPHABET, ID_LENGTH);
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}
