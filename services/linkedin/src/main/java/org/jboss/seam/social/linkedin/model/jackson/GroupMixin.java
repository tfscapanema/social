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
import org.codehaus.jackson.type.TypeReference;
import org.jboss.seam.social.linkedin.model.Group.GroupCategory;
import org.jboss.seam.social.linkedin.model.Group.GroupCount;
import org.jboss.seam.social.linkedin.model.Group.GroupPosts;
import org.jboss.seam.social.linkedin.model.Group.GroupRelation;

/**
 * 
 * @author Antoine Sabot-Durand
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class GroupMixin {

    @JsonCreator
    GroupMixin(@JsonProperty("id") Integer id, @JsonProperty("name") String name) {
    }

    @JsonProperty
    Boolean allowMemberInvites;

    @JsonProperty
    @JsonDeserialize(using = GroupCategoryDeserializer.class)
    GroupCategory category;

    @JsonProperty
    @JsonDeserialize(using = GroupCountDeserializer.class)
    List<GroupCount> countsByCategory;

    @JsonProperty
    String description;

    @JsonProperty
    Boolean isOpenToNonMembers;

    @JsonProperty
    String largeLogoUrl;

    @JsonProperty
    String locale;

    @JsonProperty
    GroupPosts posts;

    @JsonProperty
    GroupRelation relationToViewer;

    @JsonProperty
    String shortDescription;

    @JsonProperty
    String siteGroupUrl;

    @JsonProperty
    String smallLogoUrl;

    @JsonProperty
    String websiteUrl;

    private static final class GroupCategoryDeserializer extends JsonDeserializer<GroupCategory> {
        @Override
        public GroupCategory deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            JsonNode node = jp.readValueAsTree();
            return GroupCategory.valueOf(node.get("code").getTextValue().replace('-', '_').toUpperCase());
        }
    }

    private static final class GroupCountDeserializer extends JsonDeserializer<List<GroupCount>> {
        @Override
        public List<GroupCount> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDeserializationConfig(ctxt.getConfig());
            jp.setCodec(mapper);
            if (jp.hasCurrentToken()) {
                JsonNode dataNode = jp.readValueAsTree().get("values");
                if (dataNode != null) {
                    return mapper.readValue(dataNode, new TypeReference<List<GroupCount>>() {
                    });
                }
            }
            return null;
        }
    }

}
