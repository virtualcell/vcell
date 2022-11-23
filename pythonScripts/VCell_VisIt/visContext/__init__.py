import visContextAbstract

visContext = None
assert isinstance(visContext,visContextAbstract.visContextAbstract) or visContext == None

def init_with_Fake():
    from visContextFake import visContextFake
    global visContext
    visContext = visContextFake.visContextFake

def init_with_PlainVTK():
    from visContextVTK import visContextVTK
    global visContext
    visContext = visContextVTK.visContextVTK

def init_with_Visit():
    from visContextVisit import visContextVisit
    global visContext
    visContext = visContextVisit.visContextVisit

