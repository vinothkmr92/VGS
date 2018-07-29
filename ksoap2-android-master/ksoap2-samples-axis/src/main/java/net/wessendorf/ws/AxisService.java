/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.wessendorf.ws;

/**
 * @author <a href="mailto:matzew@apache.org">Matthias Webendorf</a> 
 */
public class AxisService {
    
    public CustomObject getObject(String value){
        CustomObject ret = new CustomObject();
        ret.setValue("Hello World "+value);
        return ret;
    }

}
