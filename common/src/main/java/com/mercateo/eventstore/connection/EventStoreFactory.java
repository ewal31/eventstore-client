/**
 * Copyright © 2018 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.eventstore.connection;

import java.net.InetSocketAddress;

import org.springframework.stereotype.Component;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.mercateo.eventstore.config.EventStoreProperties;
import com.mercateo.eventstore.domain.EventStoreFailure;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("eventStoreFactory")
public class EventStoreFactory {
    public Either<EventStoreFailure, EventStore> createEventStore(EventStoreProperties properties) {
        return Try.of(() -> createEventStoreInternal(properties)).toEither().mapLeft(this::mapError);
    }

    private EventStore createEventStoreInternal(EventStoreProperties properties) {
        log.info("create EvenStore client named {} with {}@{}:{}", properties.getName(), properties.getUsername(),
                properties.getHost(), properties.getPort());

        return EventStoreBuilder
            .newBuilder()
            .singleNodeAddress(InetSocketAddress.createUnresolved(properties.getHost(), properties.getPort()))
            .userCredentials(properties.getUsername(), properties.getPassword())
            .maxReconnections(-1)
            .maxOperationRetries(-1)
            .build();
    }

    private EventStoreFailure mapError(Throwable t) {
        return EventStoreFailure.of(t);
    }
}
