package ga.lupuss.anotherbikeapp.models.firebase

interface OnDocumentChanged {
    fun onNewDocument(position: Int)
    fun onDocumentDeleted(position: Int)
    fun onDocumentModified(position: Int)
}