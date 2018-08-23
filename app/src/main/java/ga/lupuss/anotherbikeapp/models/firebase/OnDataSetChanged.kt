package ga.lupuss.anotherbikeapp.models.firebase

interface OnDataSetChanged {
    fun onNewDocument(position: Int)
    fun onDocumentDeleted(position: Int)
    fun onDocumentModified(position: Int)
    fun onDataSetChanged()
}