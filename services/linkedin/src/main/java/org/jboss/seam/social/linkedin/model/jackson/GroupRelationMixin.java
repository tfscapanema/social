/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.social.linkedin.model.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.jboss.seam.social.linkedin.model.Group.GroupAvailableAction;
import org.jboss.seam.social.linkedin.model.Group.MembershipState;

/**
 * 
 * @author Antoine Sabot-Durand
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class GroupRelationMixin {

    @JsonCreator
    GroupRelationMixin(
            @JsonProperty("availableActions") @JsonDeserialize(using = AvailableActionDeserializer.class) List<GroupAvailableAction> availableActions,
            @JsonProperty("membershipState") @JsonDeserialize(using = MembershipStateDeserializer.class) MembershipState membershipState) {
    }

    private static final class AvailableActionDeserializer extends JsonDeserializer<List<GroupAvailableAction>> {
        @Override
        public List<GroupAvailableAction> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDeserializationConfig(ctxt.getConfig());
            jp.setCodec(mapper);
            List<GroupAvailableAction> actions = new ArrayList<GroupAvailableAction>();
            if (jp.hasCurrentToken()) {
                JsonNode dataNode = jp.readValueAsTree().get("values");
                if (dataNode != null) {
                    for (JsonNode d : dataNode) {
                        String s = d.path("code").getTextValue();
                        if (s != null) {
                            actions.add(GroupAvailableAction.valueOf(s.replace('-', '_').toUpperCase()));
                        }
                    }
                }
            }
            return actions;
        }
    }

    private static final class MembershipStateDeserializer extends JsonDeserializer<MembershipState> {
        @Override
        public MembershipState deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            JsonNode node = jp.readValueAsTree();
            return MembershipState.valueOf(node.get("code").getTextValue().replace('-', '_').toUpperCase());
        }
    }

}
