package br.com.alura.orgs.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityDetalhesProdutoBinding
import br.com.alura.orgs.extensions.formataParaMoedaBrasileira
import br.com.alura.orgs.extensions.tentaCarregarImagem
import br.com.alura.orgs.model.Produto
import kotlinx.coroutines.launch

private const val TAG = "DetalhesProduto"
class DetalhesProdutoActivity : AppCompatActivity() {

    private var produtoId: Long = 0L
    private var produto: Produto? = null

    private val binding by lazy {
        ActivityDetalhesProdutoBinding.inflate(layoutInflater)
    }

    private val produtoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        tentaCarregarProduto()
    }
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
                buscaProduto()
        }
    }
    private suspend fun buscaProduto() {
        produto = produtoDao.buscaPorId(produtoId)
        produto?.let {
            preencheCampos(it)
        } ?: finish()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalhes_produto, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(produto != null) {
            when(item.itemId){
                R.id.menu_detalhes_produto_remover -> {
                    lifecycleScope.launch {
                        produto?.let {
                            produtoDao.remove(it)
                            finish()
                        }
                    }
                //Log.i(TAG, "onOptionsItemSelected: remover")
                }
                R.id.menu_detalhes_produto_editar -> {
                    //Log.i(TAG, "onOptionsItemSelected: editar")
                    Intent(this, FormularioProdutoActivity::class.java).apply {
                        putExtra(CHAVE_PRODUTO_ID, produtoId)
                        startActivity(this)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun tentaCarregarProduto() {
       produtoId = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
    }
    private fun preencheCampos(produtoCarregado: Produto) {
        with(binding) {
            activityDetalhesProdutoImagem.tentaCarregarImagem(produtoCarregado.imagem)
            activityDetalhesProdutoNome.text = produtoCarregado.nome
            activityDetalhesProdutoDescricao.text = produtoCarregado.descricao
            activityDetalhesProdutoValor.text =
                produtoCarregado.valor.formataParaMoedaBrasileira()
        }
    }
}