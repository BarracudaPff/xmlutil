/*
 * Copyright (c) 2018.
 *
 * This file is part of XmlUtil.
 *
 * This file is licenced to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You should have received a copy of the license with the source distribution.
 * Alternatively, you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package nl.adaptivity.xmlutil.serialization

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import nl.adaptivity.serialutil.decodeElements
import nl.adaptivity.xmlutil.Namespace
import nl.adaptivity.xmlutil.siblingsToFragment
import nl.adaptivity.xmlutil.util.CompactFragment
import nl.adaptivity.xmlutil.util.ICompactFragment
import kotlin.reflect.KClass

@Suppress("NOTHING_TO_INLINE")
inline fun CompactFragment.Companion.serializer() = CompactFragmentSerializer

@OptIn(WillBePrivate::class, kotlinx.serialization.ExperimentalSerializationApi::class)
@Serializer(forClass = CompactFragment::class)
object CompactFragmentSerializer : KSerializer<CompactFragment> {
    private val namespacesSerializer = ListSerializer(Namespace)

    override val descriptor get() = buildClassSerialDescriptor("compactFragment") {
        element("namespaces", namespacesSerializer.descriptor)
        element("content", serialDescriptor<String>())
    }

    override fun deserialize(decoder: Decoder): CompactFragment {
        return decoder.decodeStructure(descriptor) {
            readCompactFragmentContent(this)
        }
    }

    private fun readCompactFragmentContent(input: CompositeDecoder): CompactFragment {
        return if (input is XML.XmlInput) {

            input.input.run {
                next()
                siblingsToFragment()
            }
        } else {
            var namespaces: List<Namespace> = mutableListOf()
            var content = ""

            val nsIndex = 0
            val contentIndex = 1

            decodeElements(input) { elem: Int ->
                when (elem) {
                    nsIndex      -> namespaces = input.decodeSerializableElement(descriptor, elem, namespacesSerializer)
                    contentIndex -> content = input.decodeStringElement(descriptor, elem)
                }
            }
            CompactFragment(namespaces, content)
        }
    }

    override fun serialize(encoder: Encoder, value: CompactFragment) {
        serialize(encoder, value as ICompactFragment)
    }

    fun serialize(output: Encoder, value: ICompactFragment) {
        output.encodeStructure(descriptor) {
            writeCompactFragmentContent(this, descriptor, value)
        }
    }

    private fun writeCompactFragmentContent(
        encoder: CompositeEncoder,
        descriptor: SerialDescriptor,
        value: ICompactFragment
                                           ) {

        val xmlOutput = encoder as? XML.XmlOutput

        if (xmlOutput != null) {
            val writer = xmlOutput.target
            for (namespace in value.namespaces) {
                if (writer.getPrefix(namespace.namespaceURI) == null) {
                    writer.namespaceAttr(namespace)
                }
            }

            value.serialize(writer)
        } else {
            encoder.encodeSerializableElement(descriptor, 0,
                                              namespacesSerializer, value.namespaces.toList())
            encoder.encodeStringElement(descriptor, 1, value.contentString)
        }
    }


}

object ICompactFragmentSerializer : KSerializer<ICompactFragment> {

    override val descriptor: SerialDescriptor
        get() = CompactFragmentSerializer.descriptor

    override fun deserialize(decoder: Decoder): ICompactFragment {
        return CompactFragmentSerializer.deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, value: ICompactFragment) {
        CompactFragmentSerializer.serialize(encoder, value)
    }
}
