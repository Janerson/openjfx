/*
 * Copyright (c) 2011, 2017, Oracle and/or its affiliates. All rights reserved.
 */
#include "config.h"

#include "Editor.h"
#include "CachedImage.h"
#include "DataObjectJava.h"
#include "DocumentFragment.h"
#include "Frame.h"
#include "HTMLImageElement.h"
#include "HTMLInputElement.h"
#include "HTMLNames.h"
#include "HTMLParserIdioms.h"
#include "Pasteboard.h"
#include "RenderImage.h"
#include "SVGElement.h"
#include "XLinkNames.h"
#include "markup.h"

namespace WebCore {

// FIXME(arunprasad): Implemetation task is tracked at JDK-8146460.
void Editor::pasteWithPasteboard(Pasteboard* pasteboard, bool allowPlainText, MailBlockquoteHandling mailBlockquoteHandling)
{
    RefPtr<Range> range = selectedRange();
    if (!range)
        return;

    bool chosePlainText;
    RefPtr<DocumentFragment> fragment = pasteboard->documentFragment(m_frame, *range, allowPlainText, chosePlainText);
    if (fragment && shouldInsertFragment(fragment, range, EditorInsertAction::Pasted))
        pasteAsFragment(fragment.releaseNonNull(), canSmartReplaceWithPasteboard(*pasteboard), chosePlainText, mailBlockquoteHandling);
}

RefPtr<DocumentFragment> Editor::webContentFromPasteboard(Pasteboard&, Range&, bool /*allowPlainText*/, bool& /*chosePlainText*/)
{
    notImplemented();
    return RefPtr<DocumentFragment>();
}

} // namespace WebCore
