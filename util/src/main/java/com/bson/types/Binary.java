// Binary.java

/**
 *  See the NOTICE.txt file distributed with this work for
 *  information regarding copyright ownership.
 *
 *  The authors license this file to you under the
 *  Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.  You may
 *  obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.bson.types;

import com.bson.BSON;

/**
   generic binary holder
 */
public class Binary {

    /**
     * Creates a Binary object with the default binary type of 0
     * @param data raw data
     */
    public Binary( byte[] data ){
        this(BSON.B_GENERAL, data);
    }

    /**
     * Creates a Binary object
     * @param type type of the field as encoded in BSON
     * @param data raw data
     */
    public Binary( byte type , byte[] data ){
        _type = type;
        _data = data;
    }

    public byte getType(){
        return _type;
    }

    public byte[] getData(){
        return _data;
    }

    public int length(){
        return _data.length;
    }

    final byte _type;
    final byte[] _data;
}
