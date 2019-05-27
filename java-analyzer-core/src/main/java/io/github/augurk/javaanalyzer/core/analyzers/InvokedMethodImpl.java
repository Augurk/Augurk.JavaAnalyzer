/*
 * Copyright 2019, Augurk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.augurk.javaanalyzer.core.analyzers;

import java.util.List;
import java.util.Objects;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import io.github.augurk.javaanalyzer.core.collectors.InvokedMethod;
import io.github.augurk.javaanalyzer.core.domain.InvocationKind;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class InvokedMethodImpl implements InvokedMethod, Signature, QualifiedName {
    private final String declaringType;
    private final List<ImmutablePair<String, String>> argumentTypes;
    private final ResolvedMethodDeclaration method;
    private final List<MethodDeclaration> interfaceDefinitions;
    private final boolean isLocal;

    public InvokedMethodImpl(ResolvedMethodDeclaration method, List<MethodDeclaration> interfaceDefinitions,
                             List<ImmutablePair<String, String>> argumentTypes, boolean isLocal) {

        this.declaringType = qualifiedNameOfDeclaringType(method);
        this.argumentTypes = argumentTypes;
        this.method = method;
        this.interfaceDefinitions = interfaceDefinitions;
        this.isLocal = isLocal;
    }

    @Override
    public String getDeclaringTypeName() {
        return declaringType;
    }

    @Override
    public boolean isLocal() {
        return isLocal;
    }

    @Override
    public InvocationKind getKind() {
        return method.isAbstract()
            ? InvocationKind.PUBLIC
            : InvocationKind.valueOf(method.accessSpecifier().name());
    }

    @Override
    public String getSignature() {
        return qualifiedSignatureOf(method);
    }

    @Override
    public List<ImmutablePair<String, String>> getArgumentTypes() {
        return argumentTypes;
    }

    @Override
    public String[] getInterfaceDefinitions() {
        return interfaceDefinitions.stream()
            .map(this::qualifiedSignatureOf)
            .toArray(String[]::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvokedMethodImpl)) return false;

        InvokedMethodImpl that = (InvokedMethodImpl) o;

        return Objects.equals(declaringType, that.declaringType)
            && Objects.equals(interfaceDefinitions, that.interfaceDefinitions)
            && Objects.equals(getSignature(), that.getSignature())
            && Objects.equals(argumentTypes.size(), that.argumentTypes.size())
            && Objects.equals(isLocal, that.isLocal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaringType, interfaceDefinitions, getSignature(), argumentTypes.size(), isLocal);
    }
}
