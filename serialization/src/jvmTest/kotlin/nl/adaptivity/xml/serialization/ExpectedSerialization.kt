/*
 * Copyright (c) 2021.
 *
 * This file is part of xmlutil.
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

package nl.adaptivity.xml.serialization

import org.xmlunit.assertj.CompareAssert
import org.xmlunit.assertj.XmlAssert

actual object ExpectedSerialization : DefaultExpectedSerialization() {
    override val valueContainerWithSpacesXml: String
        get() = "<valueContainer>    \nfoobar</valueContainer>"
    override val valueContainerWithSpacesAlternativeXml: String
        get() = "<valueContainer><![CDATA[    \nfoo]]>bar</valueContainer>"
    override val valueContainerWithSpacesJson: String
        get() = "{\"content\":\"    \\nfoobar\"}"
    override val valueContainerWithSpacesObj: ValueContainerTestWithSpaces.ValueContainer
        get() = ValueContainerTestWithSpaces.ValueContainer("    \nfoobar")

    override val classWithImplicitChildNamespaceXml: String
        get() = "<xo:namespaced xmlns:xo=\"http://example.org\" xmlns:p3=\"http://example.org/2\" xmlns=\"urn:foobar\" p3:Elem3=\"bla\" elem4=\"lalala\" Elem5=\"tada\"><xo:elem1>foo</xo:elem1><p2:Elem2 xmlns:p2=\"urn:myurn\">bar</p2:Elem2></xo:namespaced>"
}
