package in.weekend.unitune.libs;


import in.weekend.unitune.exceptions.SnowflakeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Snowflake {

    // Always 0 to make the whole id positive, for sorting purposes
    private static final Integer UNUSED_BITS = 1;

    private static final Integer EPOCH_TIME_BITS = 41;
    private static final Integer WORKER_ID_BITS = 10;
    private static final Integer SEQUENCE_NO_BITS = 12;
    private static final Long MAX_WORKER_ID_LIMIT = (1L << WORKER_ID_BITS) - 1;
    private static final Long MAX_SEQUENCE_NO_LIMIT = (1L << SEQUENCE_NO_BITS) - 1;
    private static final Long CUSTOM_START_EPOCH_TIME = 1577817000000L;
    private static volatile Snowflake INSTANCE;


    private Long sequenceNo = 0L;
    private Long lastUsedEpoch = -1L;

    private final Long workerId;


    @Autowired
    Snowflake(@Value("${snowflake.worker-id}") Long workerId) {
        if (workerId < 0L || workerId > MAX_WORKER_ID_LIMIT) {
            throw new SnowflakeException("WorkerId not in limits, please check worker id configuration");
        }
        this.workerId = workerId;
        INSTANCE = this;
    }

    public static Long generateId() {
        if (INSTANCE == null) {
            throw new SnowflakeException("Snowflake not initialized. Make sure Spring context is loaded.");
        }
        return INSTANCE.nextId();
    }

    public Long nextId() {
        Long currentEpoch = currentEpoch();

        // not valid, last made id time should be less
        if (currentEpoch < lastUsedEpoch) {
            throw new SnowflakeException("Time error, current time is less than last used time");
        }

        if (currentEpoch.equals(lastUsedEpoch)) {
            if (sequenceNo >= MAX_SEQUENCE_NO_LIMIT) {
                currentEpoch = waitTillNextMS(currentEpoch);
                sequenceNo = 0L;
            } else {
                sequenceNo++;
            }
        } else {
            sequenceNo = 0L;
        }

        lastUsedEpoch = currentEpoch;

        // id generation part
        return currentEpoch << (WORKER_ID_BITS + SEQUENCE_NO_BITS) | workerId << (SEQUENCE_NO_BITS) | sequenceNo;

    }

    private Long waitTillNextMS(Long currentEpoch) {
        while (currentEpoch.equals(lastUsedEpoch)) {
            currentEpoch = currentEpoch();
        }
        return currentEpoch;
    }


    private Long currentEpoch() {
        return (System.currentTimeMillis() - CUSTOM_START_EPOCH_TIME);
    }

}
