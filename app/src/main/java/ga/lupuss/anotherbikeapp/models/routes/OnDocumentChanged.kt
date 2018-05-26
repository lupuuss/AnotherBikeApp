package ga.lupuss.anotherbikeapp.models.routes

interface OnDocumentChanged {
    fun onNewDocument(position: Int)
    fun onDocumentDeleted(position: Int)
    fun onRouteModified(position: Int)
}