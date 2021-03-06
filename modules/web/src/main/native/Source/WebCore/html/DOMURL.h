/*
 * Copyright (C) 2011 Google Inc. All rights reserved.
 * Copyright (C) 2012 Motorola Mobility Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY APPLE AND ITS CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL APPLE OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#pragma once

#include "ExceptionOr.h"
#include "URL.h"
#include "URLUtils.h"
#include <wtf/HashSet.h>

namespace WebCore {

class Blob;
class ScriptExecutionContext;
class URLRegistrable;
class URLSearchParams;

class DOMURL : public RefCounted<DOMURL>, public URLUtils<DOMURL> {
public:
    static ExceptionOr<Ref<DOMURL>> create(const String& url, const String& base);
    static ExceptionOr<Ref<DOMURL>> create(const String& url, const DOMURL& base);
    static ExceptionOr<Ref<DOMURL>> create(const String& url);
    ~DOMURL();

    URL href() const { return m_url; }
    ExceptionOr<void> setHref(const String& url);
    void setQuery(const String&);

    URLSearchParams& searchParams();

    static String createObjectURL(ScriptExecutionContext&, Blob&);
    static void revokeObjectURL(ScriptExecutionContext&, const String&);

    static String createPublicURL(ScriptExecutionContext&, URLRegistrable&);

private:
    DOMURL(URL&& completeURL, URL&& baseURL);

    URL m_baseURL;
    URL m_url;
    RefPtr<URLSearchParams> m_searchParams;
};

} // namespace WebCore
