/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.meshprovisioner.transport;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import no.nordicsemi.android.meshprovisioner.models.SigModel;
import no.nordicsemi.android.meshprovisioner.models.VendorModel;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Element implements Parcelable {

    public static final Creator<Element> CREATOR = new Creator<Element>() {
        @Override
        public Element createFromParcel(Parcel in) {
            return new Element(in);
        }

        @Override
        public Element[] newArray(int size) {
            return new Element[size];
        }
    };
    @Expose
    final int locationDescriptor;

    @Expose
    final Map<Integer, MeshModel> meshModels;
    @Expose
    byte[] elementAddress;

    Element(@NonNull final byte[] elementAddress, final int locationDescriptor, final Map<Integer, MeshModel> models) {
        this.elementAddress = elementAddress;
        this.locationDescriptor = locationDescriptor;
        this.meshModels = models;
    }


    Element(final int locationDescriptor, final Map<Integer, MeshModel> models) {
        this.locationDescriptor = locationDescriptor;
        this.meshModels = models;
    }

    protected Element(Parcel in) {
        elementAddress = in.createByteArray();
        locationDescriptor = in.readInt();
        meshModels = new LinkedHashMap<>();
        sortModels(in.readHashMap(MeshModel.class.getClassLoader()));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(elementAddress);
        dest.writeInt(locationDescriptor);
        dest.writeMap(meshModels);
    }

    private void sortModels(final HashMap<Integer, MeshModel> unorderedElements) {
        final Set<Integer> unorderedKeys = unorderedElements.keySet();

        final ArrayList<Integer> orderedKeys = new ArrayList<>(unorderedKeys);
        Collections.sort(orderedKeys);
        for (int key : orderedKeys) {
            meshModels.put(key, unorderedElements.get(key));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public byte[] getElementAddress() {
        return elementAddress;
    }

    public int getLocationDescriptor() {
        return locationDescriptor;
    }

    public int getSigModelCount() {
        int count = 0;
        for (Map.Entry<Integer, MeshModel> modelEntry : meshModels.entrySet()) {
            if (modelEntry.getValue() instanceof SigModel) {
                count++;
            }
        }
        return count;
    }

    public int getVendorModelCount() {
        int count = 0;
        for (Map.Entry<Integer, MeshModel> modelEntry : meshModels.entrySet()) {
            if (modelEntry.getValue() instanceof VendorModel) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a list of sig models avaialable in this element
     *
     * @return List containing sig models
     */
    public Map<Integer, MeshModel> getMeshModels() {
        return Collections.unmodifiableMap(meshModels);
    }

    public int getElementAddressInt() {
        return ByteBuffer.wrap(elementAddress).order(ByteOrder.BIG_ENDIAN).getShort();
    }
}
