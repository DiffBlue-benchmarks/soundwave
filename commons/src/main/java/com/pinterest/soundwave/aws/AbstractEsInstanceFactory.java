/**
 * Copyright 2017 Pinterest, Inc.
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
package com.pinterest.soundwave.aws;

import com.pinterest.soundwave.bean.EsInstance;

import com.amazonaws.services.ec2.model.Instance;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEsInstanceFactory {

  protected ObjectMapper mapper = new ObjectMapper();

  public abstract EsInstance createFromEC2(Instance awsInstance) throws Exception;

  public void setCloudInstanceStore(CloudInstanceStore store) {}

  protected HashMap getAwsInstanceProperties(Instance awsInstance) throws Exception {
    HashMap map = mapper.readValue(mapper.writeValueAsString(awsInstance), HashMap.class);

    if (awsInstance.getMonitoring() != null && awsInstance.getMonitoring().getState() != null) {
      //Have to comply with the current AWS_V1 schema
      map.put("monitoring", awsInstance.getMonitoring().getState().toString());
    }

    if (awsInstance.getPlacement() != null
        && awsInstance.getPlacement().getAvailabilityZone() != null) {
      //Be backward compatible for tools
      Map placement = (Map) map.get("placement");
      if (placement != null) {
        placement.put("availability_zone", awsInstance.getPlacement().getAvailabilityZone());
      }
    }
    return map;
  }
}
